begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.eval
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|eval
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
name|Map
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|map
operator|.
name|HashedMap
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|GreaterThanEqualToEvaluator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|eval
operator|.
name|StreamEvaluator
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
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
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

begin_class
DECL|class|GreaterThanEqualToEvaluatorTest
specifier|public
class|class
name|GreaterThanEqualToEvaluatorTest
extends|extends
name|LuceneTestCase
block|{
DECL|field|factory
name|StreamFactory
name|factory
decl_stmt|;
DECL|field|values
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
decl_stmt|;
DECL|method|GreaterThanEqualToEvaluatorTest
specifier|public
name|GreaterThanEqualToEvaluatorTest
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|factory
operator|=
operator|new
name|StreamFactory
argument_list|()
operator|.
name|withFunctionName
argument_list|(
literal|"gte"
argument_list|,
name|GreaterThanEqualToEvaluator
operator|.
name|class
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|HashedMap
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|gteTwoIntegers
specifier|public
name|void
name|gteTwoIntegers
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"gte(a,b)"
argument_list|)
decl_stmt|;
name|Object
name|result
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2.0
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|3.0
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|gteTwoStrings
specifier|public
name|void
name|gteTwoStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"gte(a,b)"
argument_list|)
decl_stmt|;
name|Object
name|result
decl_stmt|;
name|String
name|foo
init|=
literal|"foo"
decl_stmt|;
name|String
name|bar
init|=
literal|"bar"
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"foo bar baz"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"foo bar baz"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"foo bar baz"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"foo bar jaz"
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|false
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|foo
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|foo
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|foo
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|bar
argument_list|)
expr_stmt|;
name|result
operator|=
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|result
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|gteTwoBooleans
specifier|public
name|void
name|gteTwoBooleans
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"gte(a,b)"
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|gteDifferentTypes1
specifier|public
name|void
name|gteDifferentTypes1
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"gte(a,b)"
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|gteDifferentTypes2
specifier|public
name|void
name|gteDifferentTypes2
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"gte(a,b)"
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|gteDifferentTypes3
specifier|public
name|void
name|gteDifferentTypes3
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"gte(a,b)"
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|gteDifferentTypes4
specifier|public
name|void
name|gteDifferentTypes4
parameter_list|()
throws|throws
name|Exception
block|{
name|StreamEvaluator
name|evaluator
init|=
name|factory
operator|.
name|constructEvaluator
argument_list|(
literal|"gte(a,b)"
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|evaluator
operator|.
name|evaluate
argument_list|(
operator|new
name|Tuple
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
