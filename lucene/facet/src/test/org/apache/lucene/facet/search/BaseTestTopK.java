begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.facet.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|search
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|index
operator|.
name|RandomIndexWriter
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
name|_TestUtil
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
name|Analyzer
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
name|FacetTestBase
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
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
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
name|search
operator|.
name|params
operator|.
name|CountFacetRequest
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
name|search
operator|.
name|params
operator|.
name|FacetRequest
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
name|search
operator|.
name|params
operator|.
name|FacetSearchParams
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
name|TaxonomyWriter
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|BaseTestTopK
specifier|public
specifier|abstract
class|class
name|BaseTestTopK
extends|extends
name|FacetTestBase
block|{
DECL|field|ALPHA
specifier|protected
specifier|static
specifier|final
name|String
name|ALPHA
init|=
literal|"alpha"
decl_stmt|;
DECL|field|BETA
specifier|protected
specifier|static
specifier|final
name|String
name|BETA
init|=
literal|"beta"
decl_stmt|;
comment|/** partition sizes on which the tests are run */
DECL|field|partitionSizes
specifier|protected
specifier|static
name|int
index|[]
name|partitionSizes
init|=
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|,
literal|100
block|,
name|Integer
operator|.
name|MAX_VALUE
block|}
decl_stmt|;
comment|/** Categories are generated from range [0,maxCategory) */
DECL|field|maxCategory
specifier|protected
specifier|static
name|int
name|maxCategory
init|=
literal|5000
decl_stmt|;
DECL|field|categoriesPow2
specifier|private
specifier|static
specifier|final
name|int
name|categoriesPow2
init|=
name|maxCategory
operator|*
name|maxCategory
decl_stmt|;
DECL|field|currDoc
specifier|private
name|int
name|currDoc
decl_stmt|;
DECL|field|nextInt
specifier|private
name|int
name|nextInt
decl_stmt|;
annotation|@
name|Override
DECL|method|populateIndex
specifier|protected
name|void
name|populateIndex
parameter_list|(
name|RandomIndexWriter
name|iw
parameter_list|,
name|TaxonomyWriter
name|taxo
parameter_list|,
name|FacetIndexingParams
name|fip
parameter_list|)
throws|throws
name|IOException
block|{
name|currDoc
operator|=
operator|-
literal|1
expr_stmt|;
name|super
operator|.
name|populateIndex
argument_list|(
name|iw
argument_list|,
name|taxo
argument_list|,
name|fip
argument_list|)
expr_stmt|;
block|}
comment|/** prepare the next random int */
DECL|method|nextInt
specifier|private
name|void
name|nextInt
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
if|if
condition|(
name|currDoc
operator|==
name|doc
condition|)
block|{
return|return;
block|}
name|currDoc
operator|=
name|doc
expr_stmt|;
comment|// the code below tries to achieve non-uniform distribution of
comment|// categories. Perhaps we can use random.nextGaussian() instead,
comment|// something like nextGaussian() * stdev + maxCategory/2. Or
comment|// try to generate a Zipf distribution.
name|nextInt
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|categoriesPow2
argument_list|)
expr_stmt|;
name|nextInt
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|nextInt
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getContent
specifier|protected
name|String
name|getContent
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|nextInt
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextDouble
argument_list|()
operator|>
literal|0.1
condition|)
block|{
return|return
name|ALPHA
operator|+
literal|' '
operator|+
name|BETA
return|;
block|}
return|return
name|ALPHA
return|;
block|}
annotation|@
name|Override
DECL|method|getCategories
specifier|protected
name|List
argument_list|<
name|CategoryPath
argument_list|>
name|getCategories
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|nextInt
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|CategoryPath
name|cp
init|=
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nextInt
operator|/
literal|1000
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nextInt
operator|/
literal|100
argument_list|)
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|nextInt
operator|/
literal|10
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Adding CP: "
operator|+
name|cp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|cp
argument_list|)
return|;
block|}
DECL|method|searchParamsWithRequests
specifier|protected
name|FacetSearchParams
name|searchParamsWithRequests
parameter_list|(
name|int
name|numResults
parameter_list|,
name|FacetIndexingParams
name|fip
parameter_list|)
block|{
name|List
argument_list|<
name|FacetRequest
argument_list|>
name|facetRequests
init|=
operator|new
name|ArrayList
argument_list|<
name|FacetRequest
argument_list|>
argument_list|()
decl_stmt|;
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|numResults
argument_list|)
argument_list|)
expr_stmt|;
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|numResults
argument_list|)
argument_list|)
expr_stmt|;
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
literal|"1"
argument_list|,
literal|"10"
argument_list|)
argument_list|,
name|numResults
argument_list|)
argument_list|)
expr_stmt|;
name|facetRequests
operator|.
name|add
argument_list|(
operator|new
name|CountFacetRequest
argument_list|(
operator|new
name|CategoryPath
argument_list|(
literal|"a"
argument_list|,
literal|"2"
argument_list|,
literal|"26"
argument_list|,
literal|"267"
argument_list|)
argument_list|,
name|numResults
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|getFacetSearchParams
argument_list|(
name|facetRequests
argument_list|,
name|fip
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|numDocsToIndex
specifier|protected
name|int
name|numDocsToIndex
parameter_list|()
block|{
return|return
literal|20000
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexWriterConfig
specifier|protected
name|IndexWriterConfig
name|getIndexWriterConfig
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
return|return
name|super
operator|.
name|getIndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|500
argument_list|,
literal|10000
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

