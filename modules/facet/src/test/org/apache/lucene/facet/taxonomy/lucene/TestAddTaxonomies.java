begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.lucene
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|RAMDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|TaxonomyReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyWriter
operator|.
name|DiskOrdinalMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyWriter
operator|.
name|MemoryOrdinalMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|lucene
operator|.
name|LuceneTaxonomyWriter
operator|.
name|OrdinalMap
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestAddTaxonomies
specifier|public
class|class
name|TestAddTaxonomies
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|test1
specifier|public
name|void
name|test1
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir1
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|LuceneTaxonomyWriter
name|tw1
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|dir1
argument_list|)
decl_stmt|;
name|tw1
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Mark Twain"
argument_list|)
argument_list|)
expr_stmt|;
name|tw1
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Animals"
argument_list|,
literal|"Dog"
argument_list|)
argument_list|)
expr_stmt|;
name|Directory
name|dir2
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|LuceneTaxonomyWriter
name|tw2
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|dir2
argument_list|)
decl_stmt|;
name|tw2
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Rob Pike"
argument_list|)
argument_list|)
expr_stmt|;
name|tw2
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Aardvarks"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|)
expr_stmt|;
name|tw2
operator|.
name|close
argument_list|()
expr_stmt|;
name|Directory
name|dir3
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|LuceneTaxonomyWriter
name|tw3
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|dir3
argument_list|)
decl_stmt|;
name|tw3
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Author"
argument_list|,
literal|"Zebra Smith"
argument_list|)
argument_list|)
expr_stmt|;
name|tw3
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Aardvarks"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|)
expr_stmt|;
name|tw3
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"Aardvarks"
argument_list|,
literal|"Aaron"
argument_list|)
argument_list|)
expr_stmt|;
name|tw3
operator|.
name|close
argument_list|()
expr_stmt|;
name|MemoryOrdinalMap
index|[]
name|maps
init|=
operator|new
name|MemoryOrdinalMap
index|[
literal|2
index|]
decl_stmt|;
name|maps
index|[
literal|0
index|]
operator|=
operator|new
name|MemoryOrdinalMap
argument_list|()
expr_stmt|;
name|maps
index|[
literal|1
index|]
operator|=
operator|new
name|MemoryOrdinalMap
argument_list|()
expr_stmt|;
name|tw1
operator|.
name|addTaxonomies
argument_list|(
operator|new
name|Directory
index|[]
block|{
name|dir2
block|,
name|dir3
block|}
argument_list|,
name|maps
argument_list|)
expr_stmt|;
name|tw1
operator|.
name|close
argument_list|()
expr_stmt|;
name|TaxonomyReader
name|tr
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|dir1
argument_list|)
decl_stmt|;
comment|// Test that the merged taxonomy now contains what we expect:
comment|// First all the categories of the original taxonomy, in their original order:
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Author"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Author/Mark Twain"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|3
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Animals"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|4
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Animals/Dog"
argument_list|)
expr_stmt|;
comment|// Then the categories new in the new taxonomy, in alphabetical order:
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|5
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Aardvarks"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|6
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Aardvarks/Aaron"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|7
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Aardvarks/Bob"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|8
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Author/Rob Pike"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getPath
argument_list|(
literal|9
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Author/Zebra Smith"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tr
operator|.
name|getSize
argument_list|()
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Test that the maps contain what we expect
name|int
index|[]
name|map0
init|=
name|maps
index|[
literal|0
index|]
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|map0
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map0
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map0
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|map0
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|map0
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|map0
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|int
index|[]
name|map1
init|=
name|maps
index|[
literal|1
index|]
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|map1
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|map1
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map1
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|map1
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|map1
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|map1
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|map1
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
block|}
comment|// A more comprehensive and big random test.
annotation|@
name|Test
DECL|method|testbig
specifier|public
name|void
name|testbig
parameter_list|()
throws|throws
name|Exception
block|{
name|dotest
argument_list|(
literal|2
argument_list|,
literal|1000
argument_list|,
literal|5000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|10
argument_list|,
literal|10000
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|50
argument_list|,
literal|20
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|10
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|50
argument_list|,
literal|20
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|10
argument_list|,
literal|1
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|10
argument_list|,
literal|1000
argument_list|,
literal|20000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|dotest
specifier|private
name|void
name|dotest
parameter_list|(
name|int
name|ntaxonomies
parameter_list|,
name|int
name|ncats
parameter_list|,
name|int
name|range
parameter_list|,
name|boolean
name|disk
parameter_list|)
throws|throws
name|Exception
block|{
name|Directory
name|dirs
index|[]
init|=
operator|new
name|Directory
index|[
name|ntaxonomies
index|]
decl_stmt|;
name|Directory
name|copydirs
index|[]
init|=
operator|new
name|Directory
index|[
name|ntaxonomies
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ntaxonomies
condition|;
name|i
operator|++
control|)
block|{
name|dirs
index|[
name|i
index|]
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|copydirs
index|[
name|i
index|]
operator|=
operator|new
name|RAMDirectory
argument_list|()
expr_stmt|;
name|LuceneTaxonomyWriter
name|tw
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|LuceneTaxonomyWriter
name|copytw
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|copydirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ncats
condition|;
name|j
operator|++
control|)
block|{
name|String
name|cat
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|random
operator|.
name|nextInt
argument_list|(
name|range
argument_list|)
argument_list|)
decl_stmt|;
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
name|cat
argument_list|)
argument_list|)
expr_stmt|;
name|copytw
operator|.
name|addCategory
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
name|cat
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// System.err.println("Taxonomy "+i+": "+tw.getSize());
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|copytw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|LuceneTaxonomyWriter
name|tw
init|=
operator|new
name|LuceneTaxonomyWriter
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|Directory
name|otherdirs
index|[]
init|=
operator|new
name|Directory
index|[
name|ntaxonomies
operator|-
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|dirs
argument_list|,
literal|1
argument_list|,
name|otherdirs
argument_list|,
literal|0
argument_list|,
name|ntaxonomies
operator|-
literal|1
argument_list|)
expr_stmt|;
name|OrdinalMap
index|[]
name|maps
init|=
operator|new
name|OrdinalMap
index|[
name|ntaxonomies
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|ntaxonomies
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ntaxonomies
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|disk
condition|)
block|{
name|maps
index|[
name|i
index|]
operator|=
operator|new
name|DiskOrdinalMap
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|,
literal|"tmpmap"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|maps
index|[
name|i
index|]
operator|=
operator|new
name|MemoryOrdinalMap
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|tw
operator|.
name|addTaxonomies
argument_list|(
name|otherdirs
argument_list|,
name|maps
argument_list|)
expr_stmt|;
comment|// System.err.println("Merged axonomy: "+tw.getSize());
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Check that all original categories in the main taxonomy remain in
comment|// unchanged, and the rest of the taxonomies are completely unchanged.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ntaxonomies
condition|;
name|i
operator|++
control|)
block|{
name|TaxonomyReader
name|tr
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|copytr
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|copydirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|tr
operator|.
name|getSize
argument_list|()
operator|>=
name|copytr
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|copytr
operator|.
name|getSize
argument_list|()
argument_list|,
name|tr
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|copytr
operator|.
name|getSize
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|expected
init|=
name|copytr
operator|.
name|getPath
argument_list|(
name|j
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|got
init|=
name|tr
operator|.
name|getPath
argument_list|(
name|j
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Comparing category "
operator|+
name|j
operator|+
literal|" of taxonomy "
operator|+
name|i
operator|+
literal|": expected "
operator|+
name|expected
operator|+
literal|", got "
operator|+
name|got
argument_list|,
name|expected
operator|.
name|equals
argument_list|(
name|got
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|copytr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Check that all the new categories in the main taxonomy are in
comment|// lexicographic order. This isn't a requirement of our API, but happens
comment|// this way in our current implementation.
name|TaxonomyReader
name|tr
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|copytr
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|copydirs
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|tr
operator|.
name|getSize
argument_list|()
operator|>
name|copytr
operator|.
name|getSize
argument_list|()
condition|)
block|{
name|String
name|prev
init|=
name|tr
operator|.
name|getPath
argument_list|(
name|copytr
operator|.
name|getSize
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|copytr
operator|.
name|getSize
argument_list|()
operator|+
literal|1
init|;
name|j
operator|<
name|tr
operator|.
name|getSize
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|n
init|=
name|tr
operator|.
name|getPath
argument_list|(
name|j
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|prev
operator|.
name|compareTo
argument_list|(
name|n
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|prev
operator|=
name|n
expr_stmt|;
block|}
block|}
name|int
name|oldsize
init|=
name|copytr
operator|.
name|getSize
argument_list|()
decl_stmt|;
comment|// remember for later
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
name|copytr
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Check that all the categories from other taxonomies exist in the new
comment|// taxonomy.
name|TaxonomyReader
name|main
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|ntaxonomies
condition|;
name|i
operator|++
control|)
block|{
name|TaxonomyReader
name|other
init|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|other
operator|.
name|getSize
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|int
name|otherord
init|=
name|main
operator|.
name|getOrdinal
argument_list|(
name|other
operator|.
name|getPath
argument_list|(
name|j
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|otherord
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
argument_list|)
expr_stmt|;
block|}
name|tr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Check that all the new categories in the merged taxonomy exist in
comment|// one of the added taxonomies.
name|TaxonomyReader
index|[]
name|others
init|=
operator|new
name|TaxonomyReader
index|[
name|ntaxonomies
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|ntaxonomies
condition|;
name|i
operator|++
control|)
block|{
name|others
index|[
name|i
operator|-
literal|1
index|]
operator|=
operator|new
name|LuceneTaxonomyReader
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
name|oldsize
init|;
name|j
operator|<
name|main
operator|.
name|getSize
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|CategoryPath
name|path
init|=
name|main
operator|.
name|getPath
argument_list|(
name|j
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|ntaxonomies
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|others
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|getOrdinal
argument_list|(
name|path
argument_list|)
operator|!=
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|fail
argument_list|(
literal|"Found category "
operator|+
name|j
operator|+
literal|" ("
operator|+
name|path
operator|+
literal|") in merged taxonomy not in any of the separate ones"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Check that all the maps are correct
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ntaxonomies
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|int
index|[]
name|map
init|=
name|maps
index|[
name|i
index|]
operator|.
name|getMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|map
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|map
index|[
name|j
index|]
argument_list|,
name|main
operator|.
name|getOrdinal
argument_list|(
name|others
index|[
name|i
index|]
operator|.
name|getPath
argument_list|(
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|ntaxonomies
condition|;
name|i
operator|++
control|)
block|{
name|others
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|main
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

