DROP FUNCTION IF EXISTS mpc_sb.UUID_TO_BIN;
DROP PROCEDURE IF EXISTS InstallMetrics_proc;

DELIMITER //
-- Port over UUID_TO_BIN functionality with swap flag enabled. SRC: https://mariadb.com/kb/en/guiduuid-performance/
CREATE DEFINER=`admin`@`%` FUNCTION `mpc_sb`.`UUID_TO_BIN`(_uuid BINARY(36)) RETURNS binary(16)
    DETERMINISTIC
    SQL SECURITY INVOKER 
BEGIN
	RETURN
        UNHEX(CONCAT(
            SUBSTR(_uuid, 15, 4),
            SUBSTR(_uuid, 10, 4),
            SUBSTR(_uuid,  1, 8),
            SUBSTR(_uuid, 20, 4),
            SUBSTR(_uuid, 25) ));
END//

CREATE DEFINER=`admin`@`%` PROCEDURE `mpc_sb`.`InstallMetrics_PROC`()
BEGIN
  DROP TEMPORARY TABLE IF EXISTS metrics_temp;
  CREATE TEMPORARY TABLE metrics_temp(
    listingId BINARY(16) NOT NULL,
    total INT NOT NULL DEFAULT 0,
    start_date date NOT NULL,
    end_date date
  );
  SET @CurrentMonth = EXTRACT(MONTH FROM NOW());
  SET @CurrentYear = EXTRACT(YEAR FROM NOW());
  SET @FirstDate = CONCAT(@CurrentYear, "-", @CurrentMonth, "-1"); 
  SET @CurrentDate = CAST(@FirstDate as date);

  SET @i = 0;
  main: LOOP
    IF (@i > 12) THEN
      LEAVE main;
    END IF;
    SET @i = @i + 1;

    -- Retrieve counts for current install period
    IF @PreviousDate = '' THEN
      INSERT INTO metrics_temp (listingId, total, start_date)
        SELECT listingId, COUNT(*) as total, @CurrentDate as start_date
          FROM Install
          	WHERE installDate >= @CurrentDate
          		GROUP BY listingId;
    ELSE
      INSERT INTO metrics_temp (listingId, total, start_date, end_date)
        SELECT listingId, COUNT(*) as total, @CurrentDate as start_date, @PreviousDate as end_date
          FROM Install
          	WHERE installDate >= @CurrentDate
              AND installDate < @PreviousDate
          		  GROUP BY listingId;
    END IF;

    -- Update the date vars for next iteration
    SET @PreviousDate = @CurrentDate;
    SET @CurrentDate = SUBDATE(@CurrentDate, INTERVAL 1 MONTH);
  END LOOP main;

  -- Remove old install metrics values (should cascade to metrics)
  DELETE FROM InstallMetrics;
  INSERT INTO InstallMetrics (id, listing_Id, total)
    SELECT UUID_TO_BIN(UUID()) as id, listingId, COUNT(*) as total
      FROM Install
        GROUP BY listingId;

  -- Bulk update the metric period table
  INSERT INTO MetricPeriod (id, listingId, count, start, end)
    SELECT UUID_TO_BIN(UUID()) as id, listingId, total, start_date, end_date
      FROM metrics_temp;

  -- Update the many-to-many associative table 
  INSERT INTO InstallMetrics_MetricPeriod
    SELECT im.id as install_metrics_id, mp.id as periods_id
      FROM MetricPeriod mp
        INNER JOIN InstallMetrics im
          ON mp.listingId = im.listing_id;
         
END
//
DELIMITER ;

