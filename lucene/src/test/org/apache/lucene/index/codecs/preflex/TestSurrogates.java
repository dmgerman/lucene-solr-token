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
comment|// like Term, but uses BytesRef for text
DECL|class|FieldAndText
specifier|private
specifier|static
class|class
name|FieldAndText
implements|implements
name|Comparable
argument_list|<
name|FieldAndText
argument_list|>
block|{
DECL|field|field
name|String
name|field
decl_stmt|;
DECL|field|text
name|BytesRef
name|text
decl_stmt|;
DECL|method|FieldAndText
specifier|public
name|FieldAndText
parameter_list|(
name|Term
name|t
parameter_list|)
block|{
name|field
operator|=
name|t
operator|.
name|field
argument_list|()
expr_stmt|;
name|text
operator|=
operator|new
name|BytesRef
argument_list|(
name|t
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|FieldAndText
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|field
operator|==
name|field
condition|)
block|{
return|return
name|text
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|text
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|field
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|field
argument_list|)
return|;
block|}
block|}
block|}
comment|// chooses from a very limited alphabet to exacerbate the
comment|// surrogate seeking required
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
operator|(
name|char
operator|)
literal|0xd800
expr_stmt|;
comment|// lo
name|buffer
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
literal|0xdc00
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
literal|'a'
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
literal|0xe000
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
DECL|method|makePreFlexSegment
specifier|private
name|SegmentInfo
name|makePreFlexSegment
parameter_list|(
name|Random
name|r
parameter_list|,
name|String
name|segName
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|FieldInfos
name|fieldInfos
parameter_list|,
name|Codec
name|codec
parameter_list|,
name|List
argument_list|<
name|FieldAndText
argument_list|>
name|fieldTerms
parameter_list|)
throws|throws
name|IOException
block|{
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
name|List
argument_list|<
name|Term
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<
name|Term
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|tc
init|=
literal|0
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
name|fieldInfos
operator|.
name|add
argument_list|(
name|field
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
comment|// The surrogate dance uses 0xffff to seek-to-end
comment|// of blocks.  Also, pre-4.0 indices are already
comment|// guaranteed to not contain the char 0xffff since
comment|// it's mapped during indexing:
name|s
operator|=
name|s
operator|.
name|replace
argument_list|(
operator|(
name|char
operator|)
literal|0xffff
argument_list|,
operator|(
name|char
operator|)
literal|0xfffe
argument_list|)
expr_stmt|;
block|}
name|terms
operator|.
name|add
argument_list|(
name|protoTerm
operator|.
name|createTerm
argument_list|(
name|s
operator|+
literal|"_"
operator|+
operator|(
name|tc
operator|++
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|fieldInfos
operator|.
name|write
argument_list|(
name|dir
argument_list|,
name|segName
argument_list|)
expr_stmt|;
comment|// sorts in UTF16 order, just like preflex:
name|Collections
operator|.
name|sort
argument_list|(
name|terms
argument_list|)
expr_stmt|;
name|TermInfosWriter
name|w
init|=
operator|new
name|TermInfosWriter
argument_list|(
name|dir
argument_list|,
name|segName
argument_list|,
name|fieldInfos
argument_list|,
literal|128
argument_list|)
decl_stmt|;
name|TermInfo
name|ti
init|=
operator|new
name|TermInfo
argument_list|()
decl_stmt|;
name|BytesRef
name|utf8
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|lastText
init|=
literal|null
decl_stmt|;
name|int
name|uniqueTermCount
init|=
literal|0
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
literal|"TEST: utf16 order:"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Term
name|t
range|:
name|terms
control|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfos
operator|.
name|fieldInfo
argument_list|(
name|t
operator|.
name|field
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|t
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastText
operator|!=
literal|null
operator|&&
name|lastText
operator|.
name|equals
argument_list|(
name|text
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|fieldTerms
operator|.
name|add
argument_list|(
operator|new
name|FieldAndText
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|uniqueTermCount
operator|++
expr_stmt|;
name|lastText
operator|=
name|text
expr_stmt|;
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|,
name|utf8
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
literal|"  "
operator|+
name|toHexString
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|add
argument_list|(
name|fi
operator|.
name|number
argument_list|,
name|utf8
operator|.
name|bytes
argument_list|,
name|utf8
operator|.
name|length
argument_list|,
name|ti
argument_list|)
expr_stmt|;
block|}
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|FieldAndText
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
name|t
operator|.
name|field
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
operator|.
name|utf8ToString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|dir
operator|.
name|createOutput
argument_list|(
name|segName
operator|+
literal|".prx"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|createOutput
argument_list|(
name|segName
operator|+
literal|".frq"
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// !!hack alert!! stuffing uniqueTermCount in as docCount
return|return
operator|new
name|SegmentInfo
argument_list|(
name|segName
argument_list|,
name|uniqueTermCount
argument_list|,
name|dir
argument_list|,
literal|false
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
name|codec
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
name|Directory
name|dir
init|=
operator|new
name|MockRAMDirectory
argument_list|()
decl_stmt|;
name|Codec
name|codec
init|=
operator|new
name|PreFlexCodec
argument_list|()
decl_stmt|;
name|Random
name|r
init|=
name|newRandom
argument_list|()
decl_stmt|;
name|FieldInfos
name|fieldInfos
init|=
operator|new
name|FieldInfos
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FieldAndText
argument_list|>
name|fieldTerms
init|=
operator|new
name|ArrayList
argument_list|<
name|FieldAndText
argument_list|>
argument_list|()
decl_stmt|;
name|SegmentInfo
name|si
init|=
name|makePreFlexSegment
argument_list|(
name|r
argument_list|,
literal|"_0"
argument_list|,
name|dir
argument_list|,
name|fieldInfos
argument_list|,
name|codec
argument_list|,
name|fieldTerms
argument_list|)
decl_stmt|;
comment|// hack alert!!
name|int
name|uniqueTermCount
init|=
name|si
operator|.
name|docCount
decl_stmt|;
name|FieldsProducer
name|fields
init|=
name|codec
operator|.
name|fieldsProducer
argument_list|(
operator|new
name|SegmentReadState
argument_list|(
name|dir
argument_list|,
name|si
argument_list|,
name|fieldInfos
argument_list|,
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|fields
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
literal|"\nTEST: now enum"
argument_list|)
expr_stmt|;
block|}
name|FieldsEnum
name|fieldsEnum
init|=
name|fields
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|String
name|field
decl_stmt|;
name|UnicodeUtil
operator|.
name|UTF16Result
name|utf16
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|()
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
name|UnicodeUtil
operator|.
name|UTF8toUTF16
argument_list|(
name|text
operator|.
name|bytes
argument_list|,
name|text
operator|.
name|offset
argument_list|,
name|text
operator|.
name|length
argument_list|,
name|utf16
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
literal|"got term="
operator|+
name|field
operator|+
literal|":"
operator|+
name|UnicodeUtil
operator|.
name|toHexString
argument_list|(
operator|new
name|String
argument_list|(
name|utf16
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|utf16
operator|.
name|length
argument_list|)
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
name|fieldTerms
operator|.
name|get
argument_list|(
name|termCount
argument_list|)
operator|.
name|field
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fieldTerms
operator|.
name|get
argument_list|(
name|termCount
argument_list|)
operator|.
name|text
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
name|fields
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

