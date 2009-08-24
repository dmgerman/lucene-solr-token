begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.analysis.miscellaneous
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|miscellaneous
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lucene
operator|.
name|analysis
operator|.
name|Token
import|;
end_import

begin_class
DECL|class|TestSingleTokenTokenFilter
specifier|public
class|class
name|TestSingleTokenTokenFilter
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Token
name|reusableToken
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|Token
name|token
init|=
operator|new
name|Token
argument_list|()
decl_stmt|;
name|SingleTokenTokenStream
name|ts
init|=
operator|new
name|SingleTokenTokenStream
argument_list|(
name|token
argument_list|)
decl_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|token
argument_list|,
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
argument_list|)
expr_stmt|;
name|token
operator|=
operator|new
name|Token
argument_list|(
literal|"hallo"
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|,
literal|"someType"
argument_list|)
expr_stmt|;
name|ts
operator|.
name|setToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|token
argument_list|,
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ts
operator|.
name|next
argument_list|(
name|reusableToken
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

