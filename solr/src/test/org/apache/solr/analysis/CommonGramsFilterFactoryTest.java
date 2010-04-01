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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TokenStream
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
name|Tokenizer
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
name|WhitespaceTokenizer
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
name|common
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
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|HashMap
import|;
end_import

begin_comment
comment|/**  * Tests pretty much copied from StopFilterFactoryTest We use the test files  * used by the StopFilterFactoryTest TODO: consider creating separate test files  * so this won't break if stop filter test files change  **/
end_comment

begin_class
DECL|class|CommonGramsFilterFactoryTest
specifier|public
class|class
name|CommonGramsFilterFactoryTest
extends|extends
name|BaseTokenTestCase
block|{
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
name|assertTrue
argument_list|(
literal|"loader is null and it shouldn't be"
argument_list|,
name|loader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|CommonGramsFilterFactory
name|factory
init|=
operator|new
name|CommonGramsFilterFactory
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
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"words"
argument_list|,
literal|"stop-1.txt"
argument_list|)
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"ignoreCase"
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
name|Set
argument_list|<
name|?
argument_list|>
name|words
init|=
name|factory
operator|.
name|getCommonWords
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|true
argument_list|,
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|CommonGramsFilterFactory
argument_list|()
expr_stmt|;
name|args
operator|.
name|put
argument_list|(
literal|"words"
argument_list|,
literal|"stop-1.txt, stop-2.txt"
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
name|words
operator|=
name|factory
operator|.
name|getCommonWords
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"words Size: "
operator|+
name|words
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|4
argument_list|,
name|words
operator|.
name|size
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|true
argument_list|,
name|factory
operator|.
name|isIgnoreCase
argument_list|()
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * If no words are provided, then a set of english default stopwords is used.    */
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
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
name|assertTrue
argument_list|(
literal|"loader is null and it shouldn't be"
argument_list|,
name|loader
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|CommonGramsFilterFactory
name|factory
init|=
operator|new
name|CommonGramsFilterFactory
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
argument_list|(
name|DEFAULT_VERSION_PARAM
argument_list|)
decl_stmt|;
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
name|?
argument_list|>
name|words
init|=
name|factory
operator|.
name|getCommonWords
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"words is null and it shouldn't be"
argument_list|,
name|words
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|words
operator|.
name|contains
argument_list|(
literal|"the"
argument_list|)
argument_list|)
expr_stmt|;
name|Tokenizer
name|tokenizer
init|=
operator|new
name|WhitespaceTokenizer
argument_list|(
name|DEFAULT_VERSION
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"testing the factory"
argument_list|)
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
init|=
name|factory
operator|.
name|create
argument_list|(
name|tokenizer
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|stream
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"testing"
block|,
literal|"testing_the"
block|,
literal|"the"
block|,
literal|"the_factory"
block|,
literal|"factory"
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

