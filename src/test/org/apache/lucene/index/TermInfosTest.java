begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

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
name|FSDirectory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_class
DECL|class|TermInfosTest
class|class
name|TermInfosTest
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
name|test
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" caught a "
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|+
literal|"\n with message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// FIXME: OG: remove hard-coded file names
DECL|method|test
specifier|public
specifier|static
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"words.txt"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" reading word file containing "
operator|+
name|file
operator|.
name|length
argument_list|()
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
name|Date
name|start
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Vector
name|keys
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|FileInputStream
name|ws
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BufferedReader
name|wr
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|ws
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
init|=
name|wr
operator|.
name|readLine
argument_list|()
init|;
name|key
operator|!=
literal|null
condition|;
name|key
operator|=
name|wr
operator|.
name|readLine
argument_list|()
control|)
name|keys
operator|.
name|addElement
argument_list|(
operator|new
name|Term
argument_list|(
literal|"word"
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|wr
operator|.
name|close
argument_list|()
expr_stmt|;
name|Date
name|end
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to read "
operator|+
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|" words"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|Random
name|gen
init|=
operator|new
name|Random
argument_list|(
literal|1251971
argument_list|)
decl_stmt|;
name|long
name|fp
init|=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
decl_stmt|;
name|long
name|pp
init|=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
decl_stmt|;
name|int
index|[]
name|docFreqs
init|=
operator|new
name|int
index|[
name|keys
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|long
index|[]
name|freqPointers
init|=
operator|new
name|long
index|[
name|keys
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|long
index|[]
name|proxPointers
init|=
operator|new
name|long
index|[
name|keys
operator|.
name|size
argument_list|()
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
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|docFreqs
index|[
name|i
index|]
operator|=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
expr_stmt|;
name|freqPointers
index|[
name|i
index|]
operator|=
name|fp
expr_stmt|;
name|proxPointers
index|[
name|i
index|]
operator|=
name|pp
expr_stmt|;
name|fp
operator|+=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
expr_stmt|;
empty_stmt|;
name|pp
operator|+=
operator|(
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|0xF
operator|)
operator|+
literal|1
expr_stmt|;
empty_stmt|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to generate values"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|Directory
name|store
init|=
name|FSDirectory
operator|.
name|getDirectory
argument_list|(
literal|"test.store"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FieldInfos
name|fis
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
name|TermInfosWriter
name|writer
init|=
operator|new
name|TermInfosWriter
argument_list|(
name|store
argument_list|,
literal|"words"
argument_list|,
name|fis
argument_list|)
decl_stmt|;
name|fis
operator|.
name|add
argument_list|(
literal|"word"
argument_list|,
literal|false
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
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|writer
operator|.
name|add
argument_list|(
operator|(
name|Term
operator|)
name|keys
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|TermInfo
argument_list|(
name|docFreqs
index|[
name|i
index|]
argument_list|,
name|freqPointers
index|[
name|i
index|]
argument_list|,
name|proxPointers
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to write table"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" table occupies "
operator|+
name|store
operator|.
name|fileLength
argument_list|(
literal|"words.tis"
argument_list|)
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|TermInfosReader
name|reader
init|=
operator|new
name|TermInfosReader
argument_list|(
name|store
argument_list|,
literal|"words"
argument_list|,
name|fis
argument_list|)
decl_stmt|;
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to open table"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|SegmentTermEnum
name|enum
type|= (
name|SegmentTermEnum
decl_stmt|)reader.terms(
block|)
function|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
enum_decl|enum.
name|next
argument_list|()
expr_stmt|;
name|Term
name|key
init|=
operator|(
name|Term
operator|)
name|keys
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
expr|enum
operator|.
name|term
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong term: "
operator|+
expr|enum
operator|.
name|term
argument_list|()
operator|+
literal|", expected: "
operator|+
name|key
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
name|TermInfo
name|ti
init|= enum
operator|.
name|termInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|ti
operator|.
name|docFreq
operator|!=
name|docFreqs
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|docFreq
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|docFreqs
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|freqPointer
operator|!=
name|freqPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|freqPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|freqPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|proxPointer
operator|!=
name|proxPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|proxPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|proxPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" milliseconds to iterate over "
operator|+
name|keys
operator|.
name|size
argument_list|()
operator|+
literal|" words"
argument_list|)
expr_stmt|;
name|start
operator|=
operator|new
name|Date
argument_list|()
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
name|keys
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Term
name|key
init|=
operator|(
name|Term
operator|)
name|keys
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|TermInfo
name|ti
init|=
name|reader
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|ti
operator|.
name|docFreq
operator|!=
name|docFreqs
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|docFreq
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|docFreqs
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|freqPointer
operator|!=
name|freqPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|freqPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|freqPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
if|if
condition|(
name|ti
operator|.
name|proxPointer
operator|!=
name|proxPointers
index|[
name|i
index|]
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"wrong value: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|ti
operator|.
name|proxPointer
argument_list|,
literal|16
argument_list|)
operator|+
literal|", expected: "
operator|+
name|Long
operator|.
name|toString
argument_list|(
name|proxPointers
index|[
name|i
index|]
argument_list|,
literal|16
argument_list|)
operator|+
literal|" at "
operator|+
name|i
argument_list|)
throw|;
block|}
name|end
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
operator|(
name|end
operator|.
name|getTime
argument_list|()
operator|-
name|start
operator|.
name|getTime
argument_list|()
operator|)
operator|/
operator|(
name|float
operator|)
name|keys
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" average milliseconds per lookup"
argument_list|)
expr_stmt|;
name|TermEnum
name|e
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
literal|"word"
argument_list|,
literal|"azz"
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Word after azz is "
operator|+
name|e
operator|.
name|term
argument_list|()
operator|.
name|text
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
parameter_list|()
constructor_decl|;
name|store
operator|.
name|close
parameter_list|()
constructor_decl|;
block|}
end_class

unit|}
end_unit

