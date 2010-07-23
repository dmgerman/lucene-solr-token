begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.preflex
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|codecs
operator|.
name|preflex
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|document
operator|.
name|*
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
name|*
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
name|index
operator|.
name|*
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
name|index
operator|.
name|codecs
operator|.
name|preflexrw
operator|.
name|PreFlexRWCodec
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
DECL|class|TestSurrogates
specifier|public
class|class
name|TestSurrogates
extends|extends
name|LuceneTestCaseJ4
block|{
DECL|method|makeDifficultRandomUnicodeString
specifier|private
specifier|static
name|String
name|makeDifficultRandomUnicodeString
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
if|if
condition|(
name|end
operator|==
literal|0
condition|)
block|{
comment|// allow 0 length
return|return
literal|""
return|;
block|}
specifier|final
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
name|end
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
name|end
condition|;
name|i
operator|++
control|)
block|{
name|int
name|t
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|t
operator|&&
name|i
operator|<
name|end
operator|-
literal|1
condition|)
block|{
comment|// hi
name|buffer
index|[
name|i
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|0xd800
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// lo
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|0xdc00
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|t
operator|<=
literal|3
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|4
operator|==
name|t
condition|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
call|(
name|char
call|)
argument_list|(
literal|0xe000
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|end
argument_list|)
return|;
block|}
DECL|method|toHexString
specifier|private
name|String
name|toHexString
parameter_list|(
name|Term
name|t
parameter_list|)
block|{
return|return
name|t
operator|.
name|field
argument_list|()
operator|+
literal|":"
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getRandomString
specifier|private
name|String
name|getRandomString
parameter_list|(
name|Random
name|r
parameter_list|)
block|{
name|String
name|s
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|==
literal|1
condition|)
block|{
name|s
operator|=
name|makeDifficultRandomUnicodeString
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|=
name|_TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
DECL|class|SortTermAsUTF16Comparator
specifier|private
specifier|static
class|class
name|SortTermAsUTF16Comparator
implements|implements
name|Comparator
argument_list|<
name|Term
argument_list|>
block|{
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Term
name|o1
parameter_list|,
name|Term
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|compareToUTF16
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
DECL|field|termAsUTF16Comparator
specifier|private
specifier|static
specifier|final
name|SortTermAsUTF16Comparator
name|termAsUTF16Comparator
init|=
operator|new
name|SortTermAsUTF16Comparator
argument_list|()
decl_stmt|;
comment|// single straight enum
DECL|method|doTestStraightEnum
specifier|private
name|void
name|doTestStraightEnum
parameter_list|(
name|List
argument_list|<
name|Term
argument_list|>
name|fieldTerms
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|int
name|uniqueTermCount
parameter_list|)
throws|throws
name|IOException
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
literal|"\nTEST: top now enum reader="
operator|+
name|reader
argument_list|)
expr_stmt|;
block|}
name|FieldsEnum
name|fieldsEnum
init|=
name|MultiFields
operator|.
name|getFields
argument_list|(
name|reader
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
block|{
comment|// Test straight enum:
name|String
name|field
decl_stmt|;
name|int
name|termCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|field
operator|=
name|fieldsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|TermsEnum
name|termsEnum
init|=
name|fieldsEnum
operator|.
name|terms
argument_list|()
decl_stmt|;
name|BytesRef
name|text
decl_stmt|;
name|BytesRef
name|lastText
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|text
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|Term
name|exp
init|=
name|fieldTerms
operator|.
name|get
argument_list|(
name|termCount
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
literal|"  got term="
operator|+
name|field
operator|+
literal|":"
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|text
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"       exp="
operator|+
name|exp
operator|.
name|field
argument_list|()
operator|+
literal|":"
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|exp
operator|.
name|text
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|lastText
operator|==
literal|null
condition|)
block|{
name|lastText
operator|=
operator|new
name|BytesRef
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|lastText
operator|.
name|compareTo
argument_list|(
name|text
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|lastText
operator|.
name|copy
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|exp
operator|.
name|field
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|exp
operator|.
name|bytes
argument_list|()
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|termCount
operator|++
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
literal|"  no more terms for field="
operator|+
name|field
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
name|uniqueTermCount
argument_list|,
name|termCount
argument_list|)
expr_stmt|;
block|}
block|}
comment|// randomly seeks to term that we know exists, then next's
comment|// from there
DECL|method|doTestSeekExists
specifier|private
name|void
name|doTestSeekExists
parameter_list|(
name|Random
name|r
parameter_list|,
name|List
argument_list|<
name|Term
argument_list|>
name|fieldTerms
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TermsEnum
argument_list|>
name|tes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsEnum
argument_list|>
argument_list|()
decl_stmt|;
comment|// Test random seek to existing term, then enum:
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
literal|"\nTEST: top now seek"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|iter
operator|++
control|)
block|{
comment|// pick random field+term
name|int
name|spot
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|fieldTerms
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Term
name|term
init|=
name|fieldTerms
operator|.
name|get
argument_list|(
name|spot
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|term
operator|.
name|field
argument_list|()
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
literal|"TEST: exist seek field="
operator|+
name|field
operator|+
literal|" term="
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// seek to it
name|TermsEnum
name|te
init|=
name|tes
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|te
operator|==
literal|null
condition|)
block|{
name|te
operator|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|tes
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|te
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
literal|"  done get enum"
argument_list|)
expr_stmt|;
block|}
comment|// seek should find the term
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
argument_list|,
name|te
operator|.
name|seek
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// now .next() this many times:
name|int
name|ct
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|5
argument_list|,
literal|100
argument_list|)
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
name|ct
condition|;
name|i
operator|++
control|)
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
literal|"TEST: now next()"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|1
operator|+
name|spot
operator|+
name|i
operator|>=
name|fieldTerms
operator|.
name|size
argument_list|()
condition|)
block|{
break|break;
block|}
name|term
operator|=
name|fieldTerms
operator|.
name|get
argument_list|(
literal|1
operator|+
name|spot
operator|+
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
name|assertNull
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|BytesRef
name|t
init|=
name|te
operator|.
name|next
argument_list|()
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
literal|"  got term="
operator|+
operator|(
name|t
operator|==
literal|null
condition|?
literal|null
else|:
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|t
operator|.
name|utf8ToString
argument_list|()
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"       exp="
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|term
operator|.
name|text
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|doTestSeekDoesNotExist
specifier|private
name|void
name|doTestSeekDoesNotExist
parameter_list|(
name|Random
name|r
parameter_list|,
name|int
name|numField
parameter_list|,
name|List
argument_list|<
name|Term
argument_list|>
name|fieldTerms
parameter_list|,
name|Term
index|[]
name|fieldTermsArray
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|TermsEnum
argument_list|>
name|tes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TermsEnum
argument_list|>
argument_list|()
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
literal|"TEST: top random seeks"
argument_list|)
expr_stmt|;
block|}
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
literal|100
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
condition|;
name|iter
operator|++
control|)
block|{
comment|// seek to random spot
name|String
name|field
init|=
operator|(
literal|"f"
operator|+
name|r
operator|.
name|nextInt
argument_list|(
name|numField
argument_list|)
operator|)
operator|.
name|intern
argument_list|()
decl_stmt|;
name|Term
name|tx
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|getRandomString
argument_list|(
name|r
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|spot
init|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|fieldTermsArray
argument_list|,
name|tx
argument_list|)
decl_stmt|;
if|if
condition|(
name|spot
operator|<
literal|0
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
literal|"TEST: non-exist seek to "
operator|+
name|field
operator|+
literal|":"
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|tx
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// term does not exist:
name|TermsEnum
name|te
init|=
name|tes
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|te
operator|==
literal|null
condition|)
block|{
name|te
operator|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|tes
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|te
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
literal|"  got enum"
argument_list|)
expr_stmt|;
block|}
name|spot
operator|=
operator|-
name|spot
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|spot
operator|==
name|fieldTerms
operator|.
name|size
argument_list|()
operator|||
name|fieldTerms
operator|.
name|get
argument_list|(
name|spot
argument_list|)
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
argument_list|,
name|te
operator|.
name|seek
argument_list|(
name|tx
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|NOT_FOUND
argument_list|,
name|te
operator|.
name|seek
argument_list|(
name|tx
operator|.
name|bytes
argument_list|()
argument_list|)
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
literal|"  got term="
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|te
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  exp term="
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|fieldTerms
operator|.
name|get
argument_list|(
name|spot
argument_list|)
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|fieldTerms
operator|.
name|get
argument_list|(
name|spot
argument_list|)
operator|.
name|bytes
argument_list|()
argument_list|,
name|te
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
comment|// now .next() this many times:
name|int
name|ct
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|5
argument_list|,
literal|100
argument_list|)
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
name|ct
condition|;
name|i
operator|++
control|)
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
literal|"TEST: now next()"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|1
operator|+
name|spot
operator|+
name|i
operator|>=
name|fieldTerms
operator|.
name|size
argument_list|()
condition|)
block|{
break|break;
block|}
name|Term
name|term
init|=
name|fieldTerms
operator|.
name|get
argument_list|(
literal|1
operator|+
name|spot
operator|+
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|term
operator|.
name|field
argument_list|()
operator|!=
name|field
condition|)
block|{
name|assertNull
argument_list|(
name|te
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|BytesRef
name|t
init|=
name|te
operator|.
name|next
argument_list|()
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
literal|"  got term="
operator|+
operator|(
name|t
operator|==
literal|null
condition|?
literal|null
else|:
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|t
operator|.
name|utf8ToString
argument_list|()
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"       exp="
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
name|term
operator|.
name|text
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|term
operator|.
name|bytes
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testSurrogatesOrder
specifier|public
name|void
name|testSurrogatesOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|r
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|r
argument_list|,
name|dir
argument_list|,
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|()
argument_list|)
operator|.
name|setCodecProvider
argument_list|(
name|_TestUtil
operator|.
name|alwaysCodec
argument_list|(
operator|new
name|PreFlexRWCodec
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numField
init|=
name|_TestUtil
operator|.
name|nextInt
argument_list|(
name|r
argument_list|,
literal|2
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|int
name|uniqueTermCount
init|=
literal|0
decl_stmt|;
name|int
name|tc
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Term
argument_list|>
name|fieldTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|f
init|=
literal|0
init|;
name|f
operator|<
name|numField
condition|;
name|f
operator|++
control|)
block|{
name|String
name|field
init|=
literal|"f"
operator|+
name|f
decl_stmt|;
name|Term
name|protoTerm
init|=
operator|new
name|Term
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numTerms
init|=
literal|10000
operator|*
name|_TestUtil
operator|.
name|getRandomMultiplier
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|uniqueTerms
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
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
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|String
name|term
init|=
name|getRandomString
argument_list|(
name|r
argument_list|)
operator|+
literal|"_ "
operator|+
operator|(
name|tc
operator|++
operator|)
decl_stmt|;
name|uniqueTerms
operator|.
name|add
argument_list|(
name|term
argument_list|)
expr_stmt|;
name|fieldTerms
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|term
argument_list|)
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|field
argument_list|,
name|term
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|uniqueTermCount
operator|+=
name|uniqueTerms
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|w
operator|.
name|getReader
argument_list|()
decl_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|fieldTerms
argument_list|,
name|termAsUTF16Comparator
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nTEST: UTF16 order"
argument_list|)
expr_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|fieldTerms
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|toHexString
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// sorts in code point order:
name|Collections
operator|.
name|sort
argument_list|(
name|fieldTerms
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
literal|"\nTEST: codepoint order"
argument_list|)
expr_stmt|;
for|for
control|(
name|Term
name|t
range|:
name|fieldTerms
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  "
operator|+
name|toHexString
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Term
index|[]
name|fieldTermsArray
init|=
name|fieldTerms
operator|.
name|toArray
argument_list|(
operator|new
name|Term
index|[
name|fieldTerms
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|//SegmentInfo si = makePreFlexSegment(r, "_0", dir, fieldInfos, codec, fieldTerms);
comment|//FieldsProducer fields = codec.fieldsProducer(new SegmentReadState(dir, si, fieldInfos, 1024, 1));
comment|//assertNotNull(fields);
name|doTestStraightEnum
argument_list|(
name|fieldTerms
argument_list|,
name|reader
argument_list|,
name|uniqueTermCount
argument_list|)
expr_stmt|;
name|doTestSeekExists
argument_list|(
name|r
argument_list|,
name|fieldTerms
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|doTestSeekDoesNotExist
argument_list|(
name|r
argument_list|,
name|numField
argument_list|,
name|fieldTerms
argument_list|,
name|fieldTermsArray
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

