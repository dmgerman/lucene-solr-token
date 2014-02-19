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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|TestBytesRef
specifier|public
class|class
name|TestBytesRef
extends|extends
name|LuceneTestCase
block|{
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|BytesRef
name|b
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|BytesRef
operator|.
name|EMPTY_BYTES
argument_list|,
name|b
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b
operator|.
name|offset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromBytes
specifier|public
name|void
name|testFromBytes
parameter_list|()
block|{
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|'a'
block|,
operator|(
name|byte
operator|)
literal|'b'
block|,
operator|(
name|byte
operator|)
literal|'c'
block|,
operator|(
name|byte
operator|)
literal|'d'
block|}
decl_stmt|;
name|BytesRef
name|b
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bytes
argument_list|,
name|b
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|b
operator|.
name|offset
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
name|BytesRef
name|b2
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bcd"
argument_list|,
name|b2
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|b
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromChars
specifier|public
name|void
name|testFromChars
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|s2
init|=
operator|new
name|BytesRef
argument_list|(
name|s
argument_list|)
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|s
argument_list|,
name|s2
argument_list|)
expr_stmt|;
block|}
comment|// only for 4.x
name|assertEquals
argument_list|(
literal|"\uFFFF"
argument_list|,
operator|new
name|BytesRef
argument_list|(
literal|"\uFFFF"
argument_list|)
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3590, AIOOBE if you append to a bytesref with offset != 0
DECL|method|testAppend
specifier|public
name|void
name|testAppend
parameter_list|()
block|{
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|'a'
block|,
operator|(
name|byte
operator|)
literal|'b'
block|,
operator|(
name|byte
operator|)
literal|'c'
block|,
operator|(
name|byte
operator|)
literal|'d'
block|}
decl_stmt|;
name|BytesRef
name|b
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// bcd
name|b
operator|.
name|append
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bcde"
argument_list|,
name|b
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// LUCENE-3590, AIOOBE if you copy to a bytesref with offset != 0
DECL|method|testCopyBytes
specifier|public
name|void
name|testCopyBytes
parameter_list|()
block|{
name|byte
name|bytes
index|[]
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|'a'
block|,
operator|(
name|byte
operator|)
literal|'b'
block|,
operator|(
name|byte
operator|)
literal|'c'
block|,
operator|(
name|byte
operator|)
literal|'d'
block|}
decl_stmt|;
name|BytesRef
name|b
init|=
operator|new
name|BytesRef
argument_list|(
name|bytes
argument_list|,
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// bcd
name|b
operator|.
name|copyBytes
argument_list|(
operator|new
name|BytesRef
argument_list|(
literal|"bcde"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bcde"
argument_list|,
name|b
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

