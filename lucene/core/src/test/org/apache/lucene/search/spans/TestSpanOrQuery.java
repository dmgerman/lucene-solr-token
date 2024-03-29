begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
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
name|index
operator|.
name|Term
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
name|QueryUtils
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

begin_comment
comment|/** Basic tests for SpanOrQuery */
end_comment

begin_class
DECL|class|TestSpanOrQuery
specifier|public
class|class
name|TestSpanOrQuery
extends|extends
name|LuceneTestCase
block|{
DECL|method|testHashcodeEquals
specifier|public
name|void
name|testHashcodeEquals
parameter_list|()
block|{
name|SpanTermQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q3
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanOrQuery
name|or1
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
decl_stmt|;
name|SpanOrQuery
name|or2
init|=
operator|new
name|SpanOrQuery
argument_list|(
name|q2
argument_list|,
name|q3
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|or1
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|or2
argument_list|)
expr_stmt|;
name|QueryUtils
operator|.
name|checkUnequal
argument_list|(
name|or1
argument_list|,
name|or2
argument_list|)
expr_stmt|;
block|}
DECL|method|testSpanOrEmpty
specifier|public
name|void
name|testSpanOrEmpty
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanOrQuery
name|a
init|=
operator|new
name|SpanOrQuery
argument_list|()
decl_stmt|;
name|SpanOrQuery
name|b
init|=
operator|new
name|SpanOrQuery
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"empty should equal"
argument_list|,
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDifferentField
specifier|public
name|void
name|testDifferentField
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanTermQuery
name|q1
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|SpanTermQuery
name|q2
init|=
operator|new
name|SpanTermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field2"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|SpanOrQuery
argument_list|(
name|q1
argument_list|,
name|q2
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"must have same field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

