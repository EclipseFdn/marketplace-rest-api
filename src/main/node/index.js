const axios = require('axios');
const time = require('moment');
const instance = axios.create({
  timeout: 10000,
  headers: {'User-Agent': 'mpc/0.0.0'}
});
const randomWords = require('random-words');
const uuid = require('uuid');
const argv = require('yargs')
  .option('c', {
    description: 'Number of listings to generate',
    alias: 'count',
    default: 1000,
    nargs: 1
  })
  .option('b', {
    description: 'Number of listings to generate per "batch"',
    alias: 'batch',
    default: 25,
    nargs: 1
  })
  .option('i', {
    description: 'Number of installs to generate',
    alias: 'installs',
    default: 0,
    nargs: 1
  })
  .option('s', {
    description: 'Server address (e.g. http://localhost:8090)',
    alias: 'server',
    nargs: 1,
    demandOption: true
  }).argv;

const max = argv.c;
const batch = argv.b;
var moment = require('moment-timezone');
const lic_types = ["EPL-2.0", "EPL-1.0", "GPL", "MIT"];
const platforms = ["windows","macos","linux"];
const eclipseVs = ["4.6","4.7","4.8","4.9","4.10","4.11","4.12", "4.13"];
const javaVs = [5, 6, 7, 8, 9, 10, 11];
const categoryIds = [];
for (var i=0;i<200;i++) {
  categoryIds.push(uuid.v4());
}
const marketIds = [];
for (var i=0;i<5;i++) {
  marketIds.push({uuid: uuid.v4(), listings:[]});
}

run();

async function run() {
  var a = await createCategory(0);
  
  console.log("Hold on to your pants, its about to get wild")
  // wait for ~5s
  var time = Date.now() + 5000;
  while (time > Date.now()) ;
  
  var count = 0;
  while (count < max) {
	  var listings = [];
	  for (var i = 0; i < batch; i++) {
		  var result = await createListing(count++);
		  if (result != null) {
			  listings.push(result);
		  }
	  }
	  for (var listingIdx in listings) {
		  await listingCallback(listings[listingIdx]);
	  }
  }

  // create markets once listings are all generated
  await createMarket(0);
}

function shuff(arr) {
  var out = Array.from(arr);
  for (var i=0;i<out.length;i++) {
    var curr = out[i];
    var rndIdx = Math.floor(Math.random() * arr.length);
    var next = out[rndIdx];
    out[rndIdx] = curr;
    out[i] = next;
  }
  return out;
}

function splice(arr) {
  var out = [];
  var copy = shuff(arr);
  var c = Math.floor(Math.random() * arr.length - 1) + 1;
  for (var i=0; i<=c;i++) {
    out.push(copy[i]);
  }
  return out;
}

async function createListing(count) {
  console.log(`Generating listing ${count} of ${max}`);
  var json = generateJSON(uuid.v4());
  return instance.put(argv.s+"/listings/", json)
    .then(function() { return json; })
    .catch(err => console.log(err));
}

async function listingCallback(json) {
  var installs = Math.floor(Math.random()*argv.i);
  var solsCount = Math.floor(Math.random()*5) + 2;
  console.log(`Generating ${solsCount} version records for listing '${json.id}'`);
  var versions = [];
  for (var j=1;j<solsCount;j++) {
    var v = await createVersion(j, solsCount, json.id);
    if (v != null) {
      versions.push(v);
    }
  }
  if (versions.length == 0) {
	  return;
  }
  console.log(`Generating ${installs} install records for listing '${json.id}'`);
  createInstall(0, installs, json, versions);
}

async function createCategory(count) {
  if (count >= categoryIds.length) {
    return;
  }

  console.log(`Generating category record with ID '${categoryIds[count]}'`);
  await instance.put(argv.s+"/categories/", generateCategoryJSON(categoryIds[count]))
    .then(async function() { await createCategory(count + 1); })
    .catch(err => console.log(err));
}

function createMarket(count) {
  if (count >= marketIds.length) {
    return;
  }

  instance.put(argv.s+"/markets/", generateMarketJSON(marketIds[count++]))
    .then(() => createMarket(count))
    .catch(err => console.log(err));
}

function createInstall(curr, maxInstall, listing, versions) {
  if (curr >= maxInstall) {
    return;
  }
  var json = generateInstallJSON(listing, versions);
  instance.post(`${argv.s}/installs/${json['listing_id']}/${json.version}`, json)
    .then(createInstall(curr+1,maxInstall,listing,versions))
    .catch(err => console.log(err));
}

async function createVersion(curr, max, id) {
  if (curr >= max) {
    return;
  }
  var json = generateVersionJSON(curr, id);
  return instance.put(`${argv.s}/listing_versions`, json)
    .then(() => {return json})
    .catch(err => console.log(err));
}

function generateJSON(id) {
  var markets = splice(marketIds).splice(0,Math.ceil(Math.random()*2));
  for (var marketIdx in markets) {
    var currUuid = markets[marketIdx].uuid;
    for (var actualMarketIdx in marketIds) {
      if (marketIds[actualMarketIdx].uuid === currUuid) {
        marketIds[actualMarketIdx].listings.push({'id':id});
        break;
      }
    }
  }
  
  var catIdsRaw = splice(categoryIds).splice(0,Math.ceil(Math.random()*5)+1);
  var cats = [];
  for (var categoryIdIdx in catIdsRaw) {
	  cats.push({'id': catIdsRaw[categoryIdIdx]});
  }
  return {
    "id": id,
  	"title": "Sample",
  	"url": "https://jakarta.ee",
  	"teaser": randomWords({exactly:1, wordsPerString:Math.floor(Math.random()*100)})[0],
  	"body": randomWords({exactly:1, wordsPerString:Math.floor(Math.random()*300)})[0],
    "status": "draft",
  	"support_url": "https://jakarta.ee/about/faq",
  	"license_type": lic_types[Math.floor(Math.random()*lic_types.length)],
    "created": moment.tz((new Date()).toISOString(), "America/Toronto").format(),
    "changed": moment.tz((new Date()).toISOString(), "America/Toronto").format(),
  	"authors": [
  		{
  			"full_name": "Martin Lowe",
  	    "username": "autumnfound"
  		}
  	],
    "organization": {
			"name": "Eclipse Foundation"
		},
  	"tags": [
  		{
  			"name": "Build tools",
  			"url": ""
  		}
  	],
  "categories": cats,
	"screenshots": ["http://www.example.com/img/sample.png"]
  };
}

function generateCategoryJSON(id) {
  return {
    "id": id,
    "title": randomWords({exactly:1, wordsPerString:Math.ceil(Math.random()*4)})[0],
    "url": "https://www.eclipse.org"
  };
}

function generateMarketJSON(market) {
  return {
    "id": market.uuid,
    "title": randomWords({exactly:1, wordsPerString:Math.ceil(Math.random()*4)})[0],
    "url": "https://www.eclipse.org",
    "listing_ids": market.listings
  };
}

function generateInstallJSON(listing,versions) {
  var version = versions[Math.floor(Math.random()*versions.length)];
  var javaVersions = Array.from(javaVs).splice(javaVs.indexOf(version["min_java_version"]));
  var eclipseVersions = Array.from(eclipseVs).splice(eclipseVs.indexOf(version["eclipse_version"]));
  
  var daysAgo = Math.floor(Math.random() * (365*2));
  
  return {
    "install_date": time().subtract(daysAgo, 'days').format(),
    "listing_id": listing.id,
    "version": version.version,
    "java_version": shuff(javaVersions)[0],
    "os": shuff(version.platforms)[0],
    "eclipse_version": shuff(eclipseVersions)[0]
  };
}

function generateVersionJSON(name, listingId) {
  return {
    "version": name,
    "update_site_url": "",
    "listing_id": listingId,
    "eclipse_versions": splice(eclipseVs),
    "min_java_version": javaVs[Math.floor(Math.random()*javaVs.length)],
    "platforms": splice(platforms),
    "feature_ids":[]
  };
}
