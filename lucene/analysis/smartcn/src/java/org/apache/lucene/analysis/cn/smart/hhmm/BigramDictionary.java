begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteOrder
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
name|cn
operator|.
name|smart
operator|.
name|AnalyzerProfile
import|;
end_import

begin_comment
comment|/**  * SmartChineseAnalyzer Bigram dictionary.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|BigramDictionary
class|class
name|BigramDictionary
extends|extends
name|AbstractDictionary
block|{
DECL|method|BigramDictionary
specifier|private
name|BigramDictionary
parameter_list|()
block|{   }
DECL|field|WORD_SEGMENT_CHAR
specifier|public
specifier|static
specifier|final
name|char
name|WORD_SEGMENT_CHAR
init|=
literal|'@'
decl_stmt|;
DECL|field|singleInstance
specifier|private
specifier|static
name|BigramDictionary
name|singleInstance
decl_stmt|;
DECL|field|PRIME_BIGRAM_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|PRIME_BIGRAM_LENGTH
init|=
literal|402137
decl_stmt|;
comment|/*    * The word associations are stored as FNV1 hashcodes, which have a small probability of collision, but save memory.      */
DECL|field|bigramHashTable
specifier|private
name|long
index|[]
name|bigramHashTable
decl_stmt|;
DECL|field|frequencyTable
specifier|private
name|int
index|[]
name|frequencyTable
decl_stmt|;
DECL|field|max
specifier|private
name|int
name|max
init|=
literal|0
decl_stmt|;
DECL|field|repeat
specifier|private
name|int
name|repeat
init|=
literal|0
decl_stmt|;
comment|// static Logger log = Logger.getLogger(BigramDictionary.class);
DECL|method|getInstance
specifier|public
specifier|synchronized
specifier|static
name|BigramDictionary
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|singleInstance
operator|==
literal|null
condition|)
block|{
name|singleInstance
operator|=
operator|new
name|BigramDictionary
argument_list|()
expr_stmt|;
try|try
block|{
name|singleInstance
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|String
name|dictRoot
init|=
name|AnalyzerProfile
operator|.
name|ANALYSIS_DATA_DIR
decl_stmt|;
name|singleInstance
operator|.
name|load
argument_list|(
name|dictRoot
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|singleInstance
return|;
block|}
DECL|method|loadFromObj
specifier|private
name|boolean
name|loadFromObj
parameter_list|(
name|File
name|serialObj
parameter_list|)
block|{
try|try
block|{
name|loadFromInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|serialObj
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|loadFromInputStream
specifier|private
name|void
name|loadFromInputStream
parameter_list|(
name|InputStream
name|serialObjectInputStream
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|ObjectInputStream
name|input
init|=
operator|new
name|ObjectInputStream
argument_list|(
name|serialObjectInputStream
argument_list|)
decl_stmt|;
name|bigramHashTable
operator|=
operator|(
name|long
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|frequencyTable
operator|=
operator|(
name|int
index|[]
operator|)
name|input
operator|.
name|readObject
argument_list|()
expr_stmt|;
comment|// log.info("load bigram dict from serialization.");
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|saveToObj
specifier|private
name|void
name|saveToObj
parameter_list|(
name|File
name|serialObj
parameter_list|)
block|{
try|try
block|{
name|ObjectOutputStream
name|output
init|=
operator|new
name|ObjectOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|serialObj
argument_list|)
argument_list|)
decl_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|bigramHashTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeObject
argument_list|(
name|frequencyTable
argument_list|)
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// log.info("serialize bigram dict.");
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// log.warn(e.getMessage());
block|}
block|}
DECL|method|load
specifier|private
name|void
name|load
parameter_list|()
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|InputStream
name|input
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"bigramdict.mem"
argument_list|)
decl_stmt|;
name|loadFromInputStream
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|load
specifier|private
name|void
name|load
parameter_list|(
name|String
name|dictRoot
parameter_list|)
block|{
name|String
name|bigramDictPath
init|=
name|dictRoot
operator|+
literal|"/bigramdict.dct"
decl_stmt|;
name|File
name|serialObj
init|=
operator|new
name|File
argument_list|(
name|dictRoot
operator|+
literal|"/bigramdict.mem"
argument_list|)
decl_stmt|;
if|if
condition|(
name|serialObj
operator|.
name|exists
argument_list|()
operator|&&
name|loadFromObj
argument_list|(
name|serialObj
argument_list|)
condition|)
block|{      }
else|else
block|{
try|try
block|{
name|bigramHashTable
operator|=
operator|new
name|long
index|[
name|PRIME_BIGRAM_LENGTH
index|]
expr_stmt|;
name|frequencyTable
operator|=
operator|new
name|int
index|[
name|PRIME_BIGRAM_LENGTH
index|]
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
name|PRIME_BIGRAM_LENGTH
condition|;
name|i
operator|++
control|)
block|{
comment|// it is possible for a value to hash to 0, but the probability is extremely low
name|bigramHashTable
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
name|frequencyTable
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
name|loadFromFile
argument_list|(
name|bigramDictPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|saveToObj
argument_list|(
name|serialObj
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Load the datafile into this BigramDictionary    *     * @param dctFilePath path to the Bigramdictionary (bigramdict.dct)    * @throws FileNotFoundException    * @throws IOException    * @throws UnsupportedEncodingException    */
DECL|method|loadFromFile
specifier|public
name|void
name|loadFromFile
parameter_list|(
name|String
name|dctFilePath
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|i
decl_stmt|,
name|cnt
decl_stmt|,
name|length
decl_stmt|,
name|total
init|=
literal|0
decl_stmt|;
comment|// The file only counted 6763 Chinese characters plus 5 reserved slots 3756~3760.
comment|// The 3756th is used (as a header) to store information.
name|int
index|[]
name|buffer
init|=
operator|new
name|int
index|[
literal|3
index|]
decl_stmt|;
name|byte
index|[]
name|intBuffer
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|String
name|tmpword
decl_stmt|;
name|RandomAccessFile
name|dctFile
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|dctFilePath
argument_list|,
literal|"r"
argument_list|)
decl_stmt|;
comment|// GB2312 characters 0 - 6768
for|for
control|(
name|i
operator|=
name|GB2312_FIRST_CHAR
init|;
name|i
operator|<
name|GB2312_FIRST_CHAR
operator|+
name|CHAR_NUM_IN_FILE
condition|;
name|i
operator|++
control|)
block|{
name|String
name|currentStr
init|=
name|getCCByGB2312Id
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// if (i == 5231)
comment|// System.out.println(i);
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
comment|// the dictionary was developed for C, and byte order must be converted to work with Java
name|cnt
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|cnt
operator|<=
literal|0
condition|)
block|{
continue|continue;
block|}
name|total
operator|+=
name|cnt
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|j
operator|<
name|cnt
condition|)
block|{
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|0
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// frequency
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
name|buffer
index|[
literal|1
index|]
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|intBuffer
argument_list|)
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
comment|// length
name|dctFile
operator|.
name|read
argument_list|(
name|intBuffer
argument_list|)
expr_stmt|;
comment|// buffer[2] = ByteBuffer.wrap(intBuffer).order(
comment|// ByteOrder.LITTLE_ENDIAN).getInt();// handle
name|length
operator|=
name|buffer
index|[
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|byte
index|[]
name|lchBuffer
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|dctFile
operator|.
name|read
argument_list|(
name|lchBuffer
argument_list|)
expr_stmt|;
name|tmpword
operator|=
operator|new
name|String
argument_list|(
name|lchBuffer
argument_list|,
literal|"GB2312"
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|!=
literal|3755
operator|+
name|GB2312_FIRST_CHAR
condition|)
block|{
name|tmpword
operator|=
name|currentStr
operator|+
name|tmpword
expr_stmt|;
block|}
name|char
name|carray
index|[]
init|=
name|tmpword
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|long
name|hashId
init|=
name|hash1
argument_list|(
name|carray
argument_list|)
decl_stmt|;
name|int
name|index
init|=
name|getAvaliableIndex
argument_list|(
name|hashId
argument_list|,
name|carray
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|bigramHashTable
index|[
name|index
index|]
operator|==
literal|0
condition|)
block|{
name|bigramHashTable
index|[
name|index
index|]
operator|=
name|hashId
expr_stmt|;
comment|// bigramStringTable[index] = tmpword;
block|}
name|frequencyTable
index|[
name|index
index|]
operator|+=
name|buffer
index|[
literal|0
index|]
expr_stmt|;
block|}
block|}
name|j
operator|++
expr_stmt|;
block|}
block|}
name|dctFile
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// log.info("load dictionary done! " + dctFilePath + " total:" + total);
block|}
DECL|method|getAvaliableIndex
specifier|private
name|int
name|getAvaliableIndex
parameter_list|(
name|long
name|hashId
parameter_list|,
name|char
name|carray
index|[]
parameter_list|)
block|{
name|int
name|hash1
init|=
call|(
name|int
call|)
argument_list|(
name|hashId
operator|%
name|PRIME_BIGRAM_LENGTH
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|hash2
argument_list|(
name|carray
argument_list|)
operator|%
name|PRIME_BIGRAM_LENGTH
decl_stmt|;
if|if
condition|(
name|hash1
operator|<
literal|0
condition|)
name|hash1
operator|=
name|PRIME_BIGRAM_LENGTH
operator|+
name|hash1
expr_stmt|;
if|if
condition|(
name|hash2
operator|<
literal|0
condition|)
name|hash2
operator|=
name|PRIME_BIGRAM_LENGTH
operator|+
name|hash2
expr_stmt|;
name|int
name|index
init|=
name|hash1
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
while|while
condition|(
name|bigramHashTable
index|[
name|index
index|]
operator|!=
literal|0
operator|&&
name|bigramHashTable
index|[
name|index
index|]
operator|!=
name|hashId
operator|&&
name|i
operator|<
name|PRIME_BIGRAM_LENGTH
condition|)
block|{
name|index
operator|=
operator|(
name|hash1
operator|+
name|i
operator|*
name|hash2
operator|)
operator|%
name|PRIME_BIGRAM_LENGTH
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
comment|// System.out.println(i - 1);
if|if
condition|(
name|i
operator|<
name|PRIME_BIGRAM_LENGTH
operator|&&
operator|(
name|bigramHashTable
index|[
name|index
index|]
operator|==
literal|0
operator|||
name|bigramHashTable
index|[
name|index
index|]
operator|==
name|hashId
operator|)
condition|)
block|{
return|return
name|index
return|;
block|}
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/*    * lookup the index into the frequency array.    */
DECL|method|getBigramItemIndex
specifier|private
name|int
name|getBigramItemIndex
parameter_list|(
name|char
name|carray
index|[]
parameter_list|)
block|{
name|long
name|hashId
init|=
name|hash1
argument_list|(
name|carray
argument_list|)
decl_stmt|;
name|int
name|hash1
init|=
call|(
name|int
call|)
argument_list|(
name|hashId
operator|%
name|PRIME_BIGRAM_LENGTH
argument_list|)
decl_stmt|;
name|int
name|hash2
init|=
name|hash2
argument_list|(
name|carray
argument_list|)
operator|%
name|PRIME_BIGRAM_LENGTH
decl_stmt|;
if|if
condition|(
name|hash1
operator|<
literal|0
condition|)
name|hash1
operator|=
name|PRIME_BIGRAM_LENGTH
operator|+
name|hash1
expr_stmt|;
if|if
condition|(
name|hash2
operator|<
literal|0
condition|)
name|hash2
operator|=
name|PRIME_BIGRAM_LENGTH
operator|+
name|hash2
expr_stmt|;
name|int
name|index
init|=
name|hash1
decl_stmt|;
name|int
name|i
init|=
literal|1
decl_stmt|;
name|repeat
operator|++
expr_stmt|;
while|while
condition|(
name|bigramHashTable
index|[
name|index
index|]
operator|!=
literal|0
operator|&&
name|bigramHashTable
index|[
name|index
index|]
operator|!=
name|hashId
operator|&&
name|i
operator|<
name|PRIME_BIGRAM_LENGTH
condition|)
block|{
name|index
operator|=
operator|(
name|hash1
operator|+
name|i
operator|*
name|hash2
operator|)
operator|%
name|PRIME_BIGRAM_LENGTH
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|repeat
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>
name|max
condition|)
name|max
operator|=
name|i
expr_stmt|;
block|}
comment|// System.out.println(i - 1);
if|if
condition|(
name|i
operator|<
name|PRIME_BIGRAM_LENGTH
operator|&&
name|bigramHashTable
index|[
name|index
index|]
operator|==
name|hashId
condition|)
block|{
return|return
name|index
return|;
block|}
else|else
return|return
operator|-
literal|1
return|;
block|}
DECL|method|getFrequency
specifier|public
name|int
name|getFrequency
parameter_list|(
name|char
index|[]
name|carray
parameter_list|)
block|{
name|int
name|index
init|=
name|getBigramItemIndex
argument_list|(
name|carray
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|!=
operator|-
literal|1
condition|)
return|return
name|frequencyTable
index|[
name|index
index|]
return|;
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

