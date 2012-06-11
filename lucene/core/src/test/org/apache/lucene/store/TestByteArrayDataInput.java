begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_class
DECL|class|TestByteArrayDataInput
specifier|public
class|class
name|TestByteArrayDataInput
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|65
block|}
decl_stmt|;
name|ByteArrayDataInput
name|in
init|=
operator|new
name|ByteArrayDataInput
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
name|bytes
operator|=
operator|new
name|byte
index|[]
block|{
literal|1
block|,
literal|1
block|,
literal|65
block|}
expr_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A"
argument_list|,
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

