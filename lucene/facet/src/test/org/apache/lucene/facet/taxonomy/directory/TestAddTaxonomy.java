begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.directory
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
name|directory
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|FacetTestCase
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
name|FacetLabel
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
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
name|directory
operator|.
name|DirectoryTaxonomyWriter
operator|.
name|OrdinalMap
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
name|util
operator|.
name|IOUtils
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
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestAddTaxonomy
specifier|public
class|class
name|TestAddTaxonomy
extends|extends
name|FacetTestCase
block|{
DECL|method|dotest
specifier|private
name|void
name|dotest
parameter_list|(
name|int
name|ncats
parameter_list|,
specifier|final
name|int
name|range
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|numCats
init|=
operator|new
name|AtomicInteger
argument_list|(
name|ncats
argument_list|)
decl_stmt|;
name|Directory
name|dirs
index|[]
init|=
operator|new
name|Directory
index|[
literal|2
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
name|dirs
operator|.
name|length
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
name|newDirectory
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryTaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Thread
index|[]
name|addThreads
init|=
operator|new
name|Thread
index|[
literal|4
index|]
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
name|addThreads
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|addThreads
index|[
name|j
index|]
operator|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
while|while
condition|(
name|numCats
operator|.
name|decrementAndGet
argument_list|()
operator|>
literal|0
condition|)
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
try|try
block|{
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|,
name|cat
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|addThreads
control|)
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|addThreads
control|)
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|DirectoryTaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|OrdinalMap
name|map
init|=
name|randomOrdinalMap
argument_list|()
decl_stmt|;
name|tw
operator|.
name|addTaxonomy
argument_list|(
name|dirs
index|[
literal|1
index|]
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|dirs
index|[
literal|0
index|]
argument_list|,
name|dirs
index|[
literal|1
index|]
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
block|}
DECL|method|randomOrdinalMap
specifier|private
name|OrdinalMap
name|randomOrdinalMap
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
return|return
operator|new
name|DiskOrdinalMap
argument_list|(
name|createTempFile
argument_list|(
literal|"taxoMap"
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MemoryOrdinalMap
argument_list|()
return|;
block|}
block|}
DECL|method|validate
specifier|private
name|void
name|validate
parameter_list|(
name|Directory
name|dest
parameter_list|,
name|Directory
name|src
parameter_list|,
name|OrdinalMap
name|ordMap
parameter_list|)
throws|throws
name|Exception
block|{
name|DirectoryTaxonomyReader
name|destTR
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dest
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|destSize
init|=
name|destTR
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyReader
name|srcTR
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|src
argument_list|)
decl_stmt|;
try|try
block|{
name|int
index|[]
name|map
init|=
name|ordMap
operator|.
name|getMap
argument_list|()
decl_stmt|;
comment|// validate taxo sizes
name|int
name|srcSize
init|=
name|srcTR
operator|.
name|getSize
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"destination taxonomy expected to be larger than source; dest="
operator|+
name|destSize
operator|+
literal|" src="
operator|+
name|srcSize
argument_list|,
name|destSize
operator|>=
name|srcSize
argument_list|)
expr_stmt|;
comment|// validate that all source categories exist in destination, and their
comment|// ordinals are as expected.
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|srcSize
condition|;
name|j
operator|++
control|)
block|{
name|FacetLabel
name|cp
init|=
name|srcTR
operator|.
name|getPath
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|int
name|destOrdinal
init|=
name|destTR
operator|.
name|getOrdinal
argument_list|(
name|cp
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cp
operator|+
literal|" not found in destination"
argument_list|,
name|destOrdinal
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|destOrdinal
argument_list|,
name|map
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|srcTR
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|destTR
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testAddEmpty
specifier|public
name|void
name|testAddEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dest
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|destTW
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|destTW
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"Author"
argument_list|,
literal|"Rob Pike"
argument_list|)
argument_list|)
expr_stmt|;
name|destTW
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"Aardvarks"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|)
expr_stmt|;
name|destTW
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Directory
name|src
init|=
name|newDirectory
argument_list|()
decl_stmt|;
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|src
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// create an empty taxonomy
name|OrdinalMap
name|map
init|=
name|randomOrdinalMap
argument_list|()
decl_stmt|;
name|destTW
operator|.
name|addTaxonomy
argument_list|(
name|src
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|destTW
operator|.
name|close
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|dest
argument_list|,
name|src
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dest
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
DECL|method|testAddToEmpty
specifier|public
name|void
name|testAddToEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dest
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|src
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|srcTW
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|srcTW
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"Author"
argument_list|,
literal|"Rob Pike"
argument_list|)
argument_list|)
expr_stmt|;
name|srcTW
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"Aardvarks"
argument_list|,
literal|"Bob"
argument_list|)
argument_list|)
expr_stmt|;
name|srcTW
operator|.
name|close
argument_list|()
expr_stmt|;
name|DirectoryTaxonomyWriter
name|destTW
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|OrdinalMap
name|map
init|=
name|randomOrdinalMap
argument_list|()
decl_stmt|;
name|destTW
operator|.
name|addTaxonomy
argument_list|(
name|src
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|destTW
operator|.
name|close
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|dest
argument_list|,
name|src
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dest
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
comment|// A more comprehensive and big random test.
DECL|method|testBig
specifier|public
name|void
name|testBig
parameter_list|()
throws|throws
name|Exception
block|{
name|dotest
argument_list|(
literal|200
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|1000
argument_list|,
literal|20000
argument_list|)
expr_stmt|;
name|dotest
argument_list|(
literal|400000
argument_list|,
literal|1000000
argument_list|)
expr_stmt|;
block|}
comment|// a reasonable random test
DECL|method|testMedium
specifier|public
name|void
name|testMedium
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|random
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|numTests
init|=
name|atLeast
argument_list|(
literal|3
argument_list|)
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
name|numTests
condition|;
name|i
operator|++
control|)
block|{
name|dotest
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|2
argument_list|,
literal|100
argument_list|)
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dest
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|tw1
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|tw1
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
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
name|FacetLabel
argument_list|(
literal|"Animals"
argument_list|,
literal|"Dog"
argument_list|)
argument_list|)
expr_stmt|;
name|tw1
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"Author"
argument_list|,
literal|"Rob Pike"
argument_list|)
argument_list|)
expr_stmt|;
name|Directory
name|src
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|tw2
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|src
argument_list|)
decl_stmt|;
name|tw2
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
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
name|FacetLabel
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
name|OrdinalMap
name|map
init|=
name|randomOrdinalMap
argument_list|()
decl_stmt|;
name|tw1
operator|.
name|addTaxonomy
argument_list|(
name|src
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|tw1
operator|.
name|close
argument_list|()
expr_stmt|;
name|validate
argument_list|(
name|dest
argument_list|,
name|src
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dest
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
DECL|method|testConcurrency
specifier|public
name|void
name|testConcurrency
parameter_list|()
throws|throws
name|Exception
block|{
comment|// tests that addTaxonomy and addCategory work in parallel
specifier|final
name|int
name|numCategories
init|=
name|atLeast
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
comment|// build an input taxonomy index
name|Directory
name|src
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryTaxonomyWriter
name|tw
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|src
argument_list|)
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
name|numCategories
condition|;
name|i
operator|++
control|)
block|{
name|tw
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tw
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now add the taxonomy to an empty taxonomy, while adding the categories
comment|// again, in parallel -- in the end, no duplicate categories should exist.
name|Directory
name|dest
init|=
name|newDirectory
argument_list|()
decl_stmt|;
specifier|final
name|DirectoryTaxonomyWriter
name|destTW
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
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
name|numCategories
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|destTW
operator|.
name|addCategory
argument_list|(
operator|new
name|FacetLabel
argument_list|(
literal|"a"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// shouldn't happen - if it does, let the test fail on uncaught exception.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|OrdinalMap
name|map
init|=
operator|new
name|MemoryOrdinalMap
argument_list|()
decl_stmt|;
name|destTW
operator|.
name|addTaxonomy
argument_list|(
name|src
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
name|destTW
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now validate
name|DirectoryTaxonomyReader
name|dtr
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|dest
argument_list|)
decl_stmt|;
comment|// +2 to account for the root category + "a"
name|assertEquals
argument_list|(
name|numCategories
operator|+
literal|2
argument_list|,
name|dtr
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|HashSet
argument_list|<
name|FacetLabel
argument_list|>
name|categories
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|dtr
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FacetLabel
name|cat
init|=
name|dtr
operator|.
name|getPath
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"category "
operator|+
name|cat
operator|+
literal|" already existed"
argument_list|,
name|categories
operator|.
name|add
argument_list|(
name|cat
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|dtr
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|src
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

