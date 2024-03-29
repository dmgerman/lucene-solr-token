begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.similarities
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|similarities
package|;
end_package

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
name|similarities
operator|.
name|ClassicSimilarity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/**  * Tests {@link ClassicSimilarityFactory} when specified on a per-fieldtype basis with various init options.  * @see SchemaSimilarityFactory  */
end_comment

begin_class
DECL|class|TestClassicSimilarityFactory
specifier|public
class|class
name|TestClassicSimilarityFactory
extends|extends
name|BaseSimilarityTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-tfidf.xml"
argument_list|)
expr_stmt|;
block|}
comment|/** Classic w/ default parameters */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassicSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text"
argument_list|,
name|ClassicSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|sim
operator|.
name|getDiscountOverlaps
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Classic w/ explicit params */
DECL|method|testParams
specifier|public
name|void
name|testParams
parameter_list|()
throws|throws
name|Exception
block|{
name|ClassicSimilarity
name|sim
init|=
name|getSimilarity
argument_list|(
literal|"text_overlap"
argument_list|,
name|ClassicSimilarity
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|sim
operator|.
name|getDiscountOverlaps
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

