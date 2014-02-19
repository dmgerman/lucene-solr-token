begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|IOContext
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
name|store
operator|.
name|IndexInput
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
name|store
operator|.
name|IndexOutput
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
name|util
operator|.
name|TestUtil
import|;
end_import

begin_class
DECL|class|TestBytesStore
specifier|public
class|class
name|TestBytesStore
extends|extends
name|LuceneTestCase
block|{
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|iters
condition|;
name|iter
operator|++
control|)
block|{
specifier|final
name|int
name|numBytes
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|200000
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|expected
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
specifier|final
name|int
name|blockBits
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|8
argument_list|,
literal|15
argument_list|)
decl_stmt|;
specifier|final
name|BytesStore
name|bytes
init|=
operator|new
name|BytesStore
argument_list|(
name|blockBits
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: iter="
operator|+
name|iter
operator|+
literal|" numBytes="
operator|+
name|numBytes
operator|+
literal|" blockBits="
operator|+
name|blockBits
argument_list|)
expr_stmt|;
block|}
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|numBytes
condition|)
block|{
name|int
name|op
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|8
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  cycle pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|op
condition|)
block|{
case|case
literal|0
case|:
block|{
comment|// write random byte
name|byte
name|b
init|=
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    writeByte b="
operator|+
name|b
argument_list|)
expr_stmt|;
block|}
name|expected
index|[
name|pos
operator|++
index|]
operator|=
name|b
expr_stmt|;
name|bytes
operator|.
name|writeByte
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|1
case|:
block|{
comment|// write random byte[]
name|int
name|len
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|numBytes
operator|-
name|pos
argument_list|,
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|temp
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|temp
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    writeBytes len="
operator|+
name|len
operator|+
literal|" bytes="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|temp
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
name|pos
argument_list|,
name|temp
operator|.
name|length
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|writeBytes
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|temp
operator|.
name|length
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
block|}
break|break;
case|case
literal|2
case|:
block|{
comment|// write int @ absolute pos
if|if
condition|(
name|pos
operator|>
literal|4
condition|)
block|{
name|int
name|x
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|int
name|randomPos
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|pos
operator|-
literal|4
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    abs writeInt pos="
operator|+
name|randomPos
operator|+
literal|" x="
operator|+
name|x
argument_list|)
expr_stmt|;
block|}
name|bytes
operator|.
name|writeInt
argument_list|(
name|randomPos
argument_list|,
name|x
argument_list|)
expr_stmt|;
name|expected
index|[
name|randomPos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|expected
index|[
name|randomPos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|expected
index|[
name|randomPos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|x
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|expected
index|[
name|randomPos
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|x
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|3
case|:
block|{
comment|// reverse bytes
if|if
condition|(
name|pos
operator|>
literal|1
condition|)
block|{
name|int
name|len
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|2
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|100
argument_list|,
name|pos
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|start
decl_stmt|;
if|if
condition|(
name|len
operator|==
name|pos
condition|)
block|{
name|start
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|pos
operator|-
name|len
argument_list|)
expr_stmt|;
block|}
name|int
name|end
init|=
name|start
operator|+
name|len
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    reverse start="
operator|+
name|start
operator|+
literal|" end="
operator|+
name|end
operator|+
literal|" len="
operator|+
name|len
operator|+
literal|" pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|bytes
operator|.
name|reverse
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
while|while
condition|(
name|start
operator|<=
name|end
condition|)
block|{
name|byte
name|b
init|=
name|expected
index|[
name|end
index|]
decl_stmt|;
name|expected
index|[
name|end
index|]
operator|=
name|expected
index|[
name|start
index|]
expr_stmt|;
name|expected
index|[
name|start
index|]
operator|=
name|b
expr_stmt|;
name|start
operator|++
expr_stmt|;
name|end
operator|--
expr_stmt|;
block|}
block|}
block|}
break|break;
case|case
literal|4
case|:
block|{
comment|// abs write random byte[]
if|if
condition|(
name|pos
operator|>
literal|2
condition|)
block|{
name|int
name|randomPos
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|pos
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|pos
operator|-
name|randomPos
operator|-
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|temp
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|temp
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    abs writeBytes pos="
operator|+
name|randomPos
operator|+
literal|" len="
operator|+
name|len
operator|+
literal|" bytes="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|temp
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|expected
argument_list|,
name|randomPos
argument_list|,
name|temp
operator|.
name|length
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|writeBytes
argument_list|(
name|randomPos
argument_list|,
name|temp
argument_list|,
literal|0
argument_list|,
name|temp
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|5
case|:
block|{
comment|// copyBytes
if|if
condition|(
name|pos
operator|>
literal|1
condition|)
block|{
name|int
name|src
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|pos
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|dest
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|src
operator|+
literal|1
argument_list|,
name|pos
operator|-
literal|1
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|300
argument_list|,
name|pos
operator|-
name|dest
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    copyBytes src="
operator|+
name|src
operator|+
literal|" dest="
operator|+
name|dest
operator|+
literal|" len="
operator|+
name|len
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|expected
argument_list|,
name|src
argument_list|,
name|expected
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|copyBytes
argument_list|(
name|src
argument_list|,
name|dest
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|6
case|:
block|{
comment|// skip
name|int
name|len
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|100
argument_list|,
name|numBytes
operator|-
name|pos
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    skip len="
operator|+
name|len
argument_list|)
expr_stmt|;
block|}
name|pos
operator|+=
name|len
expr_stmt|;
name|bytes
operator|.
name|skipBytes
argument_list|(
name|len
argument_list|)
expr_stmt|;
comment|// NOTE: must fill in zeros in case truncate was
comment|// used, else we get false fails:
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|zeros
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|bytes
operator|.
name|writeBytes
argument_list|(
name|pos
operator|-
name|len
argument_list|,
name|zeros
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
literal|7
case|:
block|{
comment|// absWriteByte
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|int
name|dest
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
name|byte
name|b
init|=
operator|(
name|byte
operator|)
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|expected
index|[
name|dest
index|]
operator|=
name|b
expr_stmt|;
name|bytes
operator|.
name|writeByte
argument_list|(
name|dest
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|bytes
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|>
literal|0
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
operator|==
literal|17
condition|)
block|{
comment|// truncate
name|int
name|len
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|pos
argument_list|,
literal|100
argument_list|)
argument_list|)
decl_stmt|;
name|bytes
operator|.
name|truncate
argument_list|(
name|pos
operator|-
name|len
argument_list|)
expr_stmt|;
name|pos
operator|-=
name|len
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|expected
argument_list|,
name|pos
argument_list|,
name|pos
operator|+
name|len
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    truncate len="
operator|+
name|len
operator|+
literal|" newPos="
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|pos
operator|>
literal|0
operator|&&
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|200
argument_list|)
operator|==
literal|17
operator|)
condition|)
block|{
name|verify
argument_list|(
name|bytes
argument_list|,
name|expected
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
block|}
name|BytesStore
name|bytesToVerify
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TEST: save/load final bytes"
argument_list|)
expr_stmt|;
block|}
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexOutput
name|out
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"bytes"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|bytes
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|in
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"bytes"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|bytesToVerify
operator|=
operator|new
name|BytesStore
argument_list|(
name|in
argument_list|,
name|numBytes
argument_list|,
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|256
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bytesToVerify
operator|=
name|bytes
expr_stmt|;
block|}
name|verify
argument_list|(
name|bytesToVerify
argument_list|,
name|expected
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verify
specifier|private
name|void
name|verify
parameter_list|(
name|BytesStore
name|bytes
parameter_list|,
name|byte
index|[]
name|expected
parameter_list|,
name|int
name|totalLength
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|totalLength
argument_list|,
name|bytes
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalLength
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  verify..."
argument_list|)
expr_stmt|;
block|}
comment|// First verify whole thing in one blast:
name|byte
index|[]
name|actual
init|=
operator|new
name|byte
index|[
name|totalLength
index|]
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    bulk: reversed"
argument_list|)
expr_stmt|;
block|}
comment|// reversed
name|FST
operator|.
name|BytesReader
name|r
init|=
name|bytes
operator|.
name|getReverseReader
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|reversed
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|setPosition
argument_list|(
name|totalLength
operator|-
literal|1
argument_list|)
expr_stmt|;
name|r
operator|.
name|readBytes
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
name|int
name|end
init|=
name|totalLength
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|start
operator|<
name|end
condition|)
block|{
name|byte
name|b
init|=
name|actual
index|[
name|start
index|]
decl_stmt|;
name|actual
index|[
name|start
index|]
operator|=
name|actual
index|[
name|end
index|]
expr_stmt|;
name|actual
index|[
name|end
index|]
operator|=
name|b
expr_stmt|;
name|start
operator|++
expr_stmt|;
name|end
operator|--
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// forward
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    bulk: forward"
argument_list|)
expr_stmt|;
block|}
name|FST
operator|.
name|BytesReader
name|r
init|=
name|bytes
operator|.
name|getForwardReader
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|reversed
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|readBytes
argument_list|(
name|actual
argument_list|,
literal|0
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|totalLength
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"byte @ index="
operator|+
name|i
argument_list|,
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|FST
operator|.
name|BytesReader
name|r
decl_stmt|;
comment|// Then verify ops:
name|boolean
name|reversed
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|reversed
condition|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    ops: reversed"
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|bytes
operator|.
name|getReverseReader
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    ops: forward"
argument_list|)
expr_stmt|;
block|}
name|r
operator|=
name|bytes
operator|.
name|getForwardReader
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|totalLength
operator|>
literal|1
condition|)
block|{
name|int
name|numOps
init|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|200
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|op
init|=
literal|0
init|;
name|op
operator|<
name|numOps
condition|;
name|op
operator|++
control|)
block|{
name|int
name|numBytes
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|1000
argument_list|,
name|totalLength
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|pos
decl_stmt|;
if|if
condition|(
name|reversed
condition|)
block|{
name|pos
operator|=
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
name|numBytes
argument_list|,
name|totalLength
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pos
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|totalLength
operator|-
name|numBytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    op iter="
operator|+
name|op
operator|+
literal|" reversed="
operator|+
name|reversed
operator|+
literal|" numBytes="
operator|+
name|numBytes
operator|+
literal|" pos="
operator|+
name|pos
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|temp
init|=
operator|new
name|byte
index|[
name|numBytes
index|]
decl_stmt|;
name|r
operator|.
name|setPosition
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pos
argument_list|,
name|r
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|.
name|readBytes
argument_list|(
name|temp
argument_list|,
literal|0
argument_list|,
name|temp
operator|.
name|length
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
name|numBytes
condition|;
name|i
operator|++
control|)
block|{
name|byte
name|expectedByte
decl_stmt|;
if|if
condition|(
name|reversed
condition|)
block|{
name|expectedByte
operator|=
name|expected
index|[
name|pos
operator|-
name|i
index|]
expr_stmt|;
block|}
else|else
block|{
name|expectedByte
operator|=
name|expected
index|[
name|pos
operator|+
name|i
index|]
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"byte @ index="
operator|+
name|i
argument_list|,
name|expectedByte
argument_list|,
name|temp
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|int
name|left
decl_stmt|;
name|int
name|expectedPos
decl_stmt|;
if|if
condition|(
name|reversed
condition|)
block|{
name|expectedPos
operator|=
name|pos
operator|-
name|numBytes
expr_stmt|;
name|left
operator|=
operator|(
name|int
operator|)
name|r
operator|.
name|getPosition
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|expectedPos
operator|=
name|pos
operator|+
name|numBytes
expr_stmt|;
name|left
operator|=
call|(
name|int
call|)
argument_list|(
name|totalLength
operator|-
name|r
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedPos
argument_list|,
name|r
operator|.
name|getPosition
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|left
operator|>
literal|4
condition|)
block|{
name|int
name|skipBytes
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|left
operator|-
literal|4
argument_list|)
decl_stmt|;
name|int
name|expectedInt
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|reversed
condition|)
block|{
name|expectedPos
operator|-=
name|skipBytes
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|--
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|--
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|--
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|--
index|]
operator|&
literal|0xFF
operator|)
expr_stmt|;
block|}
else|else
block|{
name|expectedPos
operator|+=
name|skipBytes
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|++
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
expr_stmt|;
name|expectedInt
operator||=
operator|(
name|expected
index|[
name|expectedPos
operator|++
index|]
operator|&
literal|0xFF
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    skip numBytes="
operator|+
name|skipBytes
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    readInt"
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|skipBytes
argument_list|(
name|skipBytes
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedInt
argument_list|,
name|r
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

