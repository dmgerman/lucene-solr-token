begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestCharsRef
specifier|public
class|class
name|TestCharsRef
extends|extends
name|LuceneTestCase
block|{
DECL|method|testUTF16InUTF8Order
specifier|public
name|void
name|testUTF16InUTF8Order
parameter_list|()
block|{
specifier|final
name|int
name|numStrings
init|=
name|atLeast
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|BytesRef
name|utf8
index|[]
init|=
operator|new
name|BytesRef
index|[
name|numStrings
index|]
decl_stmt|;
name|CharsRef
name|utf16
index|[]
init|=
operator|new
name|CharsRef
index|[
name|numStrings
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numStrings
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|)
decl_stmt|;
name|utf8
index|[
name|i
index|]
operator|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|utf16
index|[
name|i
index|]
operator|=
operator|new
name|CharsRef
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|utf16
argument_list|,
name|CharsRef
operator|.
name|getUTF16SortedAsUTF8Comparator
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numStrings
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|utf8
index|[
name|i
index|]
operator|.
name|utf8ToString
argument_list|()
argument_list|,
name|utf16
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

