begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|BaseTokenStreamTestCase
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
name|NumericTokenStream
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
name|util
operator|.
name|InitializationException
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
name|util
operator|.
name|ResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrResourceLoader
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Testcase for {@link TypeTokenFilterFactory}  */
end_comment

begin_class
DECL|class|TestTypeTokenFilterFactory
specifier|public
class|class
name|TestTypeTokenFilterFactory
extends|extends
name|BaseTokenStreamTestCase
block|{
annotation|@
name|Test
DECL|method|testInform
specifier|public
name|void
name|testInform
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|TypeTokenFilterFactory
name|factory
init|=
operator|new
name|TypeTokenFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"types"
argument_list|,
literal|"stoptypes-1.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
name|factory
operator|.
name|getStopTypes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"types is null and it shouldn't be"
argument_list|,
name|types
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"types Size: "
operator|+
name|types
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|types
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"enablePositionIncrements was set to true but not correctly parsed"
argument_list|,
name|factory
operator|.
name|isEnablePositionIncrements
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|TypeTokenFilterFactory
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"types"
argument_list|,
literal|"stoptypes-1.txt, stoptypes-2.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"useWhitelist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|factory
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|types
operator|=
name|factory
operator|.
name|getStopTypes
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"types is null and it shouldn't be"
argument_list|,
name|types
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"types Size: "
operator|+
name|types
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|types
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"enablePositionIncrements was set to false but not correctly parsed"
argument_list|,
operator|!
name|factory
operator|.
name|isEnablePositionIncrements
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreationWithBlackList
specifier|public
name|void
name|testCreationWithBlackList
parameter_list|()
throws|throws
name|Exception
block|{
name|TypeTokenFilterFactory
name|typeTokenFilterFactory
init|=
operator|new
name|TypeTokenFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"types"
argument_list|,
literal|"stoptypes-1.txt, stoptypes-2.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|NumericTokenStream
name|input
init|=
operator|new
name|NumericTokenStream
argument_list|()
decl_stmt|;
name|input
operator|.
name|setIntValue
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|create
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreationWithWhiteList
specifier|public
name|void
name|testCreationWithWhiteList
parameter_list|()
throws|throws
name|Exception
block|{
name|TypeTokenFilterFactory
name|typeTokenFilterFactory
init|=
operator|new
name|TypeTokenFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"types"
argument_list|,
literal|"stoptypes-1.txt, stoptypes-2.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"useWhitelist"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|NumericTokenStream
name|input
init|=
operator|new
name|NumericTokenStream
argument_list|()
decl_stmt|;
name|input
operator|.
name|setIntValue
argument_list|(
literal|123
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|create
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMissingTypesParameter
specifier|public
name|void
name|testMissingTypesParameter
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TypeTokenFilterFactory
name|typeTokenFilterFactory
init|=
operator|new
name|TypeTokenFilterFactory
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|setLuceneMatchVersion
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|typeTokenFilterFactory
operator|.
name|inform
argument_list|(
operator|new
name|SolrResourceLoader
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"not supplying 'types' parameter should cause an InitializationException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InitializationException
name|e
parameter_list|)
block|{
comment|// everything ok
block|}
block|}
block|}
end_class

end_unit

