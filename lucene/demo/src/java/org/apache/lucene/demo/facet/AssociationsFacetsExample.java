begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
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
name|ArrayList
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
name|analysis
operator|.
name|core
operator|.
name|WhitespaceAnalyzer
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
name|document
operator|.
name|Document
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
name|FacetResult
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
name|Facets
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
name|FacetsCollector
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
name|FloatAssociationFacetField
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
name|IntAssociationFacetField
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
name|TaxonomyFacetSumFloatAssociations
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
name|TaxonomyFacetSumIntAssociations
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
name|directory
operator|.
name|DirectoryTaxonomyReader
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
name|IndexWriterConfig
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|MatchAllDocsQuery
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

begin_comment
comment|/** Shows example usage of category associations. */
end_comment

begin_class
DECL|class|AssociationsFacetsExample
specifier|public
class|class
name|AssociationsFacetsExample
block|{
DECL|field|indexDir
specifier|private
specifier|final
name|Directory
name|indexDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|taxoDir
specifier|private
specifier|final
name|Directory
name|taxoDir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|FacetsConfig
name|config
decl_stmt|;
comment|/** Empty constructor */
DECL|method|AssociationsFacetsExample
specifier|public
name|AssociationsFacetsExample
parameter_list|()
block|{
name|config
operator|=
operator|new
name|FacetsConfig
argument_list|()
expr_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"tags"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"tags"
argument_list|,
literal|"$tags"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setMultiValued
argument_list|(
literal|"genre"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setIndexFieldName
argument_list|(
literal|"genre"
argument_list|,
literal|"$genre"
argument_list|)
expr_stmt|;
block|}
comment|/** Build the example index. */
DECL|method|index
specifier|private
name|void
name|index
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriterConfig
name|iwc
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|FacetExamples
operator|.
name|EXAMPLES_VER
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|(
name|FacetExamples
operator|.
name|EXAMPLES_VER
argument_list|)
argument_list|)
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexDir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
comment|// Writes facet ords to a separate directory from the main index
name|DirectoryTaxonomyWriter
name|taxoWriter
init|=
operator|new
name|DirectoryTaxonomyWriter
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
comment|// 3 occurrences for tag 'lucene'
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntAssociationFacetField
argument_list|(
literal|3
argument_list|,
literal|"tags"
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 87% confidence level of genre 'computing'
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FloatAssociationFacetField
argument_list|(
literal|0.87f
argument_list|,
literal|"genre"
argument_list|,
literal|"computing"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
comment|// 1 occurrence for tag 'lucene'
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntAssociationFacetField
argument_list|(
literal|1
argument_list|,
literal|"tags"
argument_list|,
literal|"lucene"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 2 occurrence for tag 'solr'
name|doc
operator|.
name|add
argument_list|(
operator|new
name|IntAssociationFacetField
argument_list|(
literal|2
argument_list|,
literal|"tags"
argument_list|,
literal|"solr"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 75% confidence level of genre 'computing'
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FloatAssociationFacetField
argument_list|(
literal|0.75f
argument_list|,
literal|"genre"
argument_list|,
literal|"computing"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 34% confidence level of genre 'software'
name|doc
operator|.
name|add
argument_list|(
operator|new
name|FloatAssociationFacetField
argument_list|(
literal|0.34f
argument_list|,
literal|"genre"
argument_list|,
literal|"software"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|config
operator|.
name|build
argument_list|(
name|taxoWriter
argument_list|,
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** User runs a query and aggregates facets by summing their association values. */
DECL|method|sumAssociations
specifier|private
name|List
argument_list|<
name|FacetResult
argument_list|>
name|sumAssociations
parameter_list|()
throws|throws
name|IOException
block|{
name|DirectoryReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexDir
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|TaxonomyReader
name|taxoReader
init|=
operator|new
name|DirectoryTaxonomyReader
argument_list|(
name|taxoDir
argument_list|)
decl_stmt|;
name|FacetsCollector
name|fc
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
comment|// MatchAllDocsQuery is for "browsing" (counts facets
comment|// for all non-deleted docs in the index); normally
comment|// you'd use a "normal" query:
name|FacetsCollector
operator|.
name|search
argument_list|(
name|searcher
argument_list|,
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
literal|10
argument_list|,
name|fc
argument_list|)
expr_stmt|;
name|Facets
name|tags
init|=
operator|new
name|TaxonomyFacetSumIntAssociations
argument_list|(
literal|"$tags"
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
name|Facets
name|genre
init|=
operator|new
name|TaxonomyFacetSumFloatAssociations
argument_list|(
literal|"$genre"
argument_list|,
name|taxoReader
argument_list|,
name|config
argument_list|,
name|fc
argument_list|)
decl_stmt|;
comment|// Retrieve results
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetResult
argument_list|>
argument_list|()
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|tags
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"tags"
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|genre
operator|.
name|getTopChildren
argument_list|(
literal|10
argument_list|,
literal|"genre"
argument_list|)
argument_list|)
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|taxoReader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|results
return|;
block|}
comment|/** Runs summing association example. */
DECL|method|runSumAssociations
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|runSumAssociations
parameter_list|()
throws|throws
name|IOException
block|{
name|index
argument_list|()
expr_stmt|;
return|return
name|sumAssociations
argument_list|()
return|;
block|}
comment|/** Runs the sum int/float associations examples and prints the results. */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Sum associations example:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-------------------------"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|results
init|=
operator|new
name|AssociationsFacetsExample
argument_list|()
operator|.
name|runSumAssociations
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tags: "
operator|+
name|results
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"genre: "
operator|+
name|results
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

