/* Copyright (c) 2019 Eclipse Foundation and others.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License 2.0
 * which is available at http://www.eclipse.org/legal/epl-v20.html,
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipsefoundation.marketplace.helper;

import java.util.List;
import java.util.Optional;

import org.eclipsefoundation.marketplace.helper.SortableHelper;
import org.eclipsefoundation.marketplace.helper.SortableHelper.Sortable;
import org.eclipsefoundation.marketplace.model.SortableField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.DisabledOnSubstrate;
import io.quarkus.test.junit.QuarkusTest;

/**
 * @author Martin Lowe
 *
 */
@DisabledOnSubstrate
@QuarkusTest
public class SortableHelperTest {

	@Test
	public void testGetSortableFieldsValidClass() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(CustomDocType.class);

		// check that our list gets returned
		Assertions.assertNotNull(sortables);
		Assertions.assertEquals(8, sortables.size());

		// check that all of the sortables are created and populated
		for (Sortable<?> s : sortables) {
			Assertions.assertNotNull(s.getName());
			Assertions.assertNotNull(s.getPath());
			Assertions.assertNotNull(s.getType());
		}
	}

	@Test
	public void testGetSortableFieldsNoAnnotations() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(Object.class);

		Assertions.assertNotNull(sortables);
		Assertions.assertTrue(sortables.isEmpty());
	}

	@Test
	public void testGetSortableFieldsNoClass() {
		// this should throw as the class is required
		Assertions.assertThrows(NullPointerException.class, () -> SortableHelper.getSortableFields(null));
	}

	@Test
	public void testGetSortableFieldsNested() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(CustomDocType.class);
		Optional<Sortable<?>> sOpt = sortables.stream().filter(c -> c.getName().equals("name")).findFirst();

		// assert that we have access to the nested field
		Assertions.assertTrue(sOpt.isPresent());
		Sortable<?> s = sOpt.get();
		// check that the nested type processed properly
		Assertions.assertEquals("nt.name", s.getPath());
		Assertions.assertEquals("name", s.getName());
		Assertions.assertEquals(String.class, s.getType());
	}

	@Test
	public void testGetSortableFieldsCustomName() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(CustomDocType.class);
		Optional<Sortable<?>> sOpt = sortables.stream().filter(c -> c.getName().equals("grp")).findFirst();

		// assert that we have access to the nested field
		Assertions.assertTrue(sOpt.isPresent());
		Sortable<?> s = sOpt.get();
		// check that the nested type processed properly
		Assertions.assertEquals("nt.grp", s.getPath());
		Assertions.assertEquals("grp", s.getName());
		Assertions.assertEquals(String.class, s.getType());
	}
	
	@Test
	public void testGetSortableFieldsCustomPath() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(CustomDocType.class);
		Optional<Sortable<?>> sOpt = sortables.stream().filter(c -> c.getName().equals("cat")).findFirst();

		// assert that we have access to the nested field
		Assertions.assertTrue(sOpt.isPresent());
		Sortable<?> s = sOpt.get();
		// check that the nested type processed properly
		Assertions.assertEquals("nt.grp.cat", s.getPath());
		Assertions.assertEquals("cat", s.getName());
		Assertions.assertEquals(String.class, s.getType());
	}
	
	@Test
	public void testGetSortableFieldByName() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(CustomDocType.class);
		Optional<Sortable<?>> s = SortableHelper.getSortableFieldByName(sortables, "name");

		// assert that we have access to the nested field
		Assertions.assertTrue(s.isPresent());
	}
	
	@Test
	public void testGetSortableFieldByNameCustomName() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(CustomDocType.class);

		// assert that we can find the sortable with custom name rather than the field name
		Assertions.assertTrue(SortableHelper.getSortableFieldByName(sortables, "grp").isPresent());
		Assertions.assertFalse(SortableHelper.getSortableFieldByName(sortables, "group").isPresent());
	}
	
	@Test
	public void testGetSortableFieldByNameNullName() {
		List<Sortable<?>> sortables = SortableHelper.getSortableFields(CustomDocType.class);
		Assertions.assertThrows(NullPointerException.class, () -> SortableHelper.getSortableFieldByName(sortables, null));
	}
	
	@Test
	public void testGetSortableFieldByNameNullSortables() {
		Assertions.assertThrows(NullPointerException.class, () -> SortableHelper.getSortableFieldByName(null, "sample"));
	}

	/**
	 * Custom type example with private, package-private, protected, and public
	 * fields with annotations.
	 * 
	 * @author Martin Lowe
	 */
	public static class CustomDocType {
		@SortableField
		private long id;
		@SortableField
		public int count;
		@SortableField
		long members;
		@SortableField
		protected long time;
		CustomNestedType nt;

		public CustomDocType() {
			this.nt = new CustomNestedType();
		}
	}

	public static class CustomNestedType {
		@SortableField
		String name;
		@SortableField(name = "grp")
		String group;
		@SortableField(path = "nt.grp.cat")
		String cat;
		@SortableField
		Object invalidType;
	}
}
