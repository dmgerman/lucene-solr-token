begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.taxonomy
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
package|;
end_package

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
name|List
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
name|FacetsConfig
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
name|index
operator|.
name|LeafReader
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
name|index
operator|.
name|LeafReaderContext
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
name|index
operator|.
name|DirectoryReader
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|MultiReader
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Utility methods for merging index and taxonomy directories.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|TaxonomyMergeUtils
specifier|public
specifier|abstract
class|class
name|TaxonomyMergeUtils
block|{
DECL|method|TaxonomyMergeUtils
specifier|private
name|TaxonomyMergeUtils
parameter_list|()
block|{}
comment|/**    * Merges the given taxonomy and index directories and commits the changes to    * the given writers.    */
DECL|method|merge
specifier|public
specifier|static
name|void
name|merge
parameter_list|(
name|Directory
name|srcIndexDir
parameter_list|,
name|Directory
name|srcTaxoDir
parameter_list|,
name|OrdinalMap
name|map
parameter_list|,
name|IndexWriter
name|destIndexWriter
parameter_list|,
name|DirectoryTaxonomyWriter
name|destTaxoWriter
parameter_list|,
name|FacetsConfig
name|srcConfig
parameter_list|)
throws|throws
name|IOException
block|{
comment|// merge the taxonomies
name|destTaxoWriter
operator|.
name|addTaxonomy
argument_list|(
name|srcTaxoDir
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|int
name|ordinalMap
index|[]
init|=
name|map
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|srcIndexDir
argument_list|)
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|int
name|numReaders
init|=
name|leaves
operator|.
name|size
argument_list|()
decl_stmt|;
name|LeafReader
name|wrappedLeaves
index|[]
init|=
operator|new
name|LeafReader
index|[
name|numReaders
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
name|numReaders
condition|;
name|i
operator|++
control|)
block|{
name|wrappedLeaves
index|[
name|i
index|]
operator|=
operator|new
name|OrdinalMappingLeafReader
argument_list|(
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|reader
argument_list|()
argument_list|,
name|ordinalMap
argument_list|,
name|srcConfig
argument_list|)
expr_stmt|;
block|}
name|destIndexWriter
operator|.
name|addIndexes
argument_list|(
operator|new
name|MultiReader
argument_list|(
name|wrappedLeaves
argument_list|)
argument_list|)
expr_stmt|;
comment|// commit changes to taxonomy and index respectively.
name|destTaxoWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
name|destIndexWriter
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

