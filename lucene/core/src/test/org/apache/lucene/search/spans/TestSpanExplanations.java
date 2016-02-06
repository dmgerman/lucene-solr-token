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
name|search
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * TestExplanations subclass focusing on span queries  */
end_comment

begin_class
DECL|class|TestSpanExplanations
specifier|public
class|class
name|TestSpanExplanations
extends|extends
name|BaseExplanationTestCase
block|{
comment|/* simple SpanTermQueries */
DECL|method|testST1
specifier|public
name|void
name|testST1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|st
argument_list|(
literal|"w1"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testST2
specifier|public
name|void
name|testST2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|st
argument_list|(
literal|"w1"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testST4
specifier|public
name|void
name|testST4
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|st
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testST5
specifier|public
name|void
name|testST5
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|st
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* some SpanFirstQueries */
DECL|method|testSF1
specifier|public
name|void
name|testSF1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sf
argument_list|(
operator|(
literal|"w1"
operator|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSF2
specifier|public
name|void
name|testSF2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sf
argument_list|(
operator|(
literal|"w1"
operator|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSF4
specifier|public
name|void
name|testSF4
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sf
argument_list|(
operator|(
literal|"xx"
operator|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSF5
specifier|public
name|void
name|testSF5
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sf
argument_list|(
operator|(
literal|"yy"
operator|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{ }
argument_list|)
expr_stmt|;
block|}
DECL|method|testSF6
specifier|public
name|void
name|testSF6
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sf
argument_list|(
operator|(
literal|"yy"
operator|)
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* some SpanOrQueries */
DECL|method|testSO1
specifier|public
name|void
name|testSO1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sor
argument_list|(
literal|"w1"
argument_list|,
literal|"QQ"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSO2
specifier|public
name|void
name|testSO2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sor
argument_list|(
literal|"w1"
argument_list|,
literal|"w3"
argument_list|,
literal|"zz"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSO3
specifier|public
name|void
name|testSO3
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sor
argument_list|(
literal|"w5"
argument_list|,
literal|"QQ"
argument_list|,
literal|"yy"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSO4
specifier|public
name|void
name|testSO4
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|sor
argument_list|(
literal|"w5"
argument_list|,
literal|"QQ"
argument_list|,
literal|"yy"
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* some SpanNearQueries */
DECL|method|testSNear1
specifier|public
name|void
name|testSNear1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"QQ"
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear2
specifier|public
name|void
name|testSNear2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"xx"
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear3
specifier|public
name|void
name|testSNear3
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"xx"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear4
specifier|public
name|void
name|testSNear4
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"xx"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear5
specifier|public
name|void
name|testSNear5
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"xx"
argument_list|,
literal|"w1"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear6
specifier|public
name|void
name|testSNear6
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w2"
argument_list|,
literal|"QQ"
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear7
specifier|public
name|void
name|testSNear7
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"xx"
argument_list|,
literal|"w2"
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear8
specifier|public
name|void
name|testSNear8
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"xx"
argument_list|,
literal|"w2"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear9
specifier|public
name|void
name|testSNear9
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"xx"
argument_list|,
literal|"w2"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear10
specifier|public
name|void
name|testSNear10
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"xx"
argument_list|,
literal|"w1"
argument_list|,
literal|"w2"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|2
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNear11
specifier|public
name|void
name|testSNear11
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w2"
argument_list|,
literal|"w3"
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
argument_list|)
expr_stmt|;
block|}
comment|/* some SpanNotQueries */
DECL|method|testSNot1
specifier|public
name|void
name|testSNot1
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|sf
argument_list|(
literal|"w1"
argument_list|,
literal|10
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"QQ"
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot2
specifier|public
name|void
name|testSNot2
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|sf
argument_list|(
literal|"w1"
argument_list|,
literal|10
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"QQ"
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot4
specifier|public
name|void
name|testSNot4
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|sf
argument_list|(
literal|"w1"
argument_list|,
literal|10
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot5
specifier|public
name|void
name|testSNot5
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|sf
argument_list|(
literal|"w1"
argument_list|,
literal|10
argument_list|)
argument_list|,
name|st
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|1000
argument_list|)
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot7
specifier|public
name|void
name|testSNot7
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|f
init|=
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w3"
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|f
argument_list|,
name|st
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testSNot10
specifier|public
name|void
name|testSNot10
parameter_list|()
throws|throws
name|Exception
block|{
name|SpanQuery
name|t
init|=
name|st
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|SpanQuery
name|q
init|=
name|snot
argument_list|(
name|snear
argument_list|(
literal|"w1"
argument_list|,
literal|"w3"
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
argument_list|,
name|t
argument_list|)
decl_stmt|;
name|qtest
argument_list|(
name|q
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|3
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

