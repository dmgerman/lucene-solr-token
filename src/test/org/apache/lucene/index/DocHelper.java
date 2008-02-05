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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|Analyzer
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
name|WhitespaceAnalyzer
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
name|Document
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
name|Field
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
name|Fieldable
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
name|search
operator|.
name|Similarity
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

begin_class
DECL|class|DocHelper
class|class
name|DocHelper
block|{
DECL|field|FIELD_1_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_1_TEXT
init|=
literal|"field one text"
decl_stmt|;
DECL|field|TEXT_FIELD_1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_1_KEY
init|=
literal|"textField1"
decl_stmt|;
DECL|field|textField1
specifier|public
specifier|static
name|Field
name|textField1
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_1_KEY
argument_list|,
name|FIELD_1_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
decl_stmt|;
DECL|field|FIELD_2_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_2_TEXT
init|=
literal|"field field field two text"
decl_stmt|;
comment|//Fields will be lexicographically sorted.  So, the order is: field, text, two
DECL|field|FIELD_2_FREQS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|FIELD_2_FREQS
init|=
block|{
literal|3
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
DECL|field|TEXT_FIELD_2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_2_KEY
init|=
literal|"textField2"
decl_stmt|;
DECL|field|textField2
specifier|public
specifier|static
name|Field
name|textField2
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
decl_stmt|;
DECL|field|FIELD_2_COMPRESSED_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_2_COMPRESSED_TEXT
init|=
literal|"field field field two text"
decl_stmt|;
comment|//Fields will be lexicographically sorted.  So, the order is: field, text, two
DECL|field|COMPRESSED_FIELD_2_FREQS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|COMPRESSED_FIELD_2_FREQS
init|=
block|{
literal|3
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
DECL|field|COMPRESSED_TEXT_FIELD_2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|COMPRESSED_TEXT_FIELD_2_KEY
init|=
literal|"compressedTextField2"
decl_stmt|;
DECL|field|compressedTextField2
specifier|public
specifier|static
name|Field
name|compressedTextField2
init|=
operator|new
name|Field
argument_list|(
name|COMPRESSED_TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_COMPRESSED_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|COMPRESS
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
decl_stmt|;
DECL|field|FIELD_3_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_3_TEXT
init|=
literal|"aaaNoNorms aaaNoNorms bbbNoNorms"
decl_stmt|;
DECL|field|TEXT_FIELD_3_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_3_KEY
init|=
literal|"textField3"
decl_stmt|;
DECL|field|textField3
specifier|public
specifier|static
name|Field
name|textField3
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_3_KEY
argument_list|,
name|FIELD_3_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
decl_stmt|;
static|static
block|{
name|textField3
operator|.
name|setOmitNorms
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|field|KEYWORD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|KEYWORD_TEXT
init|=
literal|"Keyword"
decl_stmt|;
DECL|field|KEYWORD_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEYWORD_FIELD_KEY
init|=
literal|"keyField"
decl_stmt|;
DECL|field|keyField
specifier|public
specifier|static
name|Field
name|keyField
init|=
operator|new
name|Field
argument_list|(
name|KEYWORD_FIELD_KEY
argument_list|,
name|KEYWORD_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|UN_TOKENIZED
argument_list|)
decl_stmt|;
DECL|field|NO_NORMS_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|NO_NORMS_TEXT
init|=
literal|"omitNormsText"
decl_stmt|;
DECL|field|NO_NORMS_KEY
specifier|public
specifier|static
specifier|final
name|String
name|NO_NORMS_KEY
init|=
literal|"omitNorms"
decl_stmt|;
DECL|field|noNormsField
specifier|public
specifier|static
name|Field
name|noNormsField
init|=
operator|new
name|Field
argument_list|(
name|NO_NORMS_KEY
argument_list|,
name|NO_NORMS_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO_NORMS
argument_list|)
decl_stmt|;
DECL|field|UNINDEXED_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNINDEXED_FIELD_TEXT
init|=
literal|"unindexed field text"
decl_stmt|;
DECL|field|UNINDEXED_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNINDEXED_FIELD_KEY
init|=
literal|"unIndField"
decl_stmt|;
DECL|field|unIndField
specifier|public
specifier|static
name|Field
name|unIndField
init|=
operator|new
name|Field
argument_list|(
name|UNINDEXED_FIELD_KEY
argument_list|,
name|UNINDEXED_FIELD_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NO
argument_list|)
decl_stmt|;
DECL|field|UNSTORED_1_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_1_FIELD_TEXT
init|=
literal|"unstored field text"
decl_stmt|;
DECL|field|UNSTORED_FIELD_1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_FIELD_1_KEY
init|=
literal|"unStoredField1"
decl_stmt|;
DECL|field|unStoredField1
specifier|public
specifier|static
name|Field
name|unStoredField1
init|=
operator|new
name|Field
argument_list|(
name|UNSTORED_FIELD_1_KEY
argument_list|,
name|UNSTORED_1_FIELD_TEXT
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
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
decl_stmt|;
DECL|field|UNSTORED_2_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_2_FIELD_TEXT
init|=
literal|"unstored field text"
decl_stmt|;
DECL|field|UNSTORED_FIELD_2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|UNSTORED_FIELD_2_KEY
init|=
literal|"unStoredField2"
decl_stmt|;
DECL|field|unStoredField2
specifier|public
specifier|static
name|Field
name|unStoredField2
init|=
operator|new
name|Field
argument_list|(
name|UNSTORED_FIELD_2_KEY
argument_list|,
name|UNSTORED_2_FIELD_TEXT
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
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|YES
argument_list|)
decl_stmt|;
DECL|field|LAZY_FIELD_BINARY_KEY
specifier|public
specifier|static
specifier|final
name|String
name|LAZY_FIELD_BINARY_KEY
init|=
literal|"lazyFieldBinary"
decl_stmt|;
DECL|field|LAZY_FIELD_BINARY_BYTES
specifier|public
specifier|static
name|byte
index|[]
name|LAZY_FIELD_BINARY_BYTES
decl_stmt|;
DECL|field|lazyFieldBinary
specifier|public
specifier|static
name|Field
name|lazyFieldBinary
decl_stmt|;
DECL|field|LAZY_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|LAZY_FIELD_KEY
init|=
literal|"lazyField"
decl_stmt|;
DECL|field|LAZY_FIELD_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|LAZY_FIELD_TEXT
init|=
literal|"These are some field bytes"
decl_stmt|;
DECL|field|lazyField
specifier|public
specifier|static
name|Field
name|lazyField
init|=
operator|new
name|Field
argument_list|(
name|LAZY_FIELD_KEY
argument_list|,
name|LAZY_FIELD_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
decl_stmt|;
DECL|field|LARGE_LAZY_FIELD_KEY
specifier|public
specifier|static
specifier|final
name|String
name|LARGE_LAZY_FIELD_KEY
init|=
literal|"largeLazyField"
decl_stmt|;
DECL|field|LARGE_LAZY_FIELD_TEXT
specifier|public
specifier|static
name|String
name|LARGE_LAZY_FIELD_TEXT
decl_stmt|;
DECL|field|largeLazyField
specifier|public
specifier|static
name|Field
name|largeLazyField
decl_stmt|;
comment|//From Issue 509
DECL|field|FIELD_UTF1_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_UTF1_TEXT
init|=
literal|"field one \u4e00text"
decl_stmt|;
DECL|field|TEXT_FIELD_UTF1_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_UTF1_KEY
init|=
literal|"textField1Utf8"
decl_stmt|;
DECL|field|textUtfField1
specifier|public
specifier|static
name|Field
name|textUtfField1
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_UTF1_KEY
argument_list|,
name|FIELD_UTF1_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|NO
argument_list|)
decl_stmt|;
DECL|field|FIELD_UTF2_TEXT
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_UTF2_TEXT
init|=
literal|"field field field \u4e00two text"
decl_stmt|;
comment|//Fields will be lexicographically sorted.  So, the order is: field, text, two
DECL|field|FIELD_UTF2_FREQS
specifier|public
specifier|static
specifier|final
name|int
index|[]
name|FIELD_UTF2_FREQS
init|=
block|{
literal|3
block|,
literal|1
block|,
literal|1
block|}
decl_stmt|;
DECL|field|TEXT_FIELD_UTF2_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TEXT_FIELD_UTF2_KEY
init|=
literal|"textField2Utf8"
decl_stmt|;
DECL|field|textUtfField2
specifier|public
specifier|static
name|Field
name|textUtfField2
init|=
operator|new
name|Field
argument_list|(
name|TEXT_FIELD_UTF2_KEY
argument_list|,
name|FIELD_UTF2_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|,
name|Field
operator|.
name|TermVector
operator|.
name|WITH_POSITIONS_OFFSETS
argument_list|)
decl_stmt|;
DECL|field|nameValues
specifier|public
specifier|static
name|Map
name|nameValues
init|=
literal|null
decl_stmt|;
comment|// ordered list of all the fields...
comment|// could use LinkedHashMap for this purpose if Java1.4 is OK
DECL|field|fields
specifier|public
specifier|static
name|Field
index|[]
name|fields
init|=
operator|new
name|Field
index|[]
block|{
name|textField1
block|,
name|textField2
block|,
name|textField3
block|,
name|compressedTextField2
block|,
name|keyField
block|,
name|noNormsField
block|,
name|unIndField
block|,
name|unStoredField1
block|,
name|unStoredField2
block|,
name|textUtfField1
block|,
name|textUtfField2
block|,
name|lazyField
block|,
name|lazyFieldBinary
block|,
comment|//placeholder for binary field, since this is null.  It must be second to last.
name|largeLazyField
comment|//placeholder for large field, since this is null.  It must always be last
block|}
decl_stmt|;
comment|// Map<String fieldName, Fieldable field>
DECL|field|all
specifier|public
specifier|static
name|Map
name|all
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|indexed
specifier|public
specifier|static
name|Map
name|indexed
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|stored
specifier|public
specifier|static
name|Map
name|stored
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|unstored
specifier|public
specifier|static
name|Map
name|unstored
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|unindexed
specifier|public
specifier|static
name|Map
name|unindexed
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|termvector
specifier|public
specifier|static
name|Map
name|termvector
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|notermvector
specifier|public
specifier|static
name|Map
name|notermvector
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|lazy
specifier|public
specifier|static
name|Map
name|lazy
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|noNorms
specifier|public
specifier|static
name|Map
name|noNorms
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
static|static
block|{
comment|//Initialize the large Lazy Field
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
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
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"Lazily loading lengths of language in lieu of laughing "
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|LAZY_FIELD_BINARY_BYTES
operator|=
literal|"These are some binary field bytes"
operator|.
name|getBytes
argument_list|(
literal|"UTF8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{     }
name|lazyFieldBinary
operator|=
operator|new
name|Field
argument_list|(
name|LAZY_FIELD_BINARY_KEY
argument_list|,
name|LAZY_FIELD_BINARY_BYTES
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
expr_stmt|;
name|fields
index|[
name|fields
operator|.
name|length
operator|-
literal|2
index|]
operator|=
name|lazyFieldBinary
expr_stmt|;
name|LARGE_LAZY_FIELD_TEXT
operator|=
name|buffer
operator|.
name|toString
argument_list|()
expr_stmt|;
name|largeLazyField
operator|=
operator|new
name|Field
argument_list|(
name|LARGE_LAZY_FIELD_KEY
argument_list|,
name|LARGE_LAZY_FIELD_TEXT
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
expr_stmt|;
name|fields
index|[
name|fields
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|largeLazyField
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Fieldable
name|f
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|add
argument_list|(
name|all
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isIndexed
argument_list|()
condition|)
name|add
argument_list|(
name|indexed
argument_list|,
name|f
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|unindexed
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isTermVectorStored
argument_list|()
condition|)
name|add
argument_list|(
name|termvector
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isIndexed
argument_list|()
operator|&&
operator|!
name|f
operator|.
name|isTermVectorStored
argument_list|()
condition|)
name|add
argument_list|(
name|notermvector
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isStored
argument_list|()
condition|)
name|add
argument_list|(
name|stored
argument_list|,
name|f
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|unstored
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|getOmitNorms
argument_list|()
condition|)
name|add
argument_list|(
name|noNorms
argument_list|,
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|isLazy
argument_list|()
condition|)
name|add
argument_list|(
name|lazy
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add
specifier|private
specifier|static
name|void
name|add
parameter_list|(
name|Map
name|map
parameter_list|,
name|Fieldable
name|field
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|field
operator|.
name|name
argument_list|()
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
static|static
block|{
name|nameValues
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_1_KEY
argument_list|,
name|FIELD_1_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|COMPRESSED_TEXT_FIELD_2_KEY
argument_list|,
name|FIELD_2_COMPRESSED_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_3_KEY
argument_list|,
name|FIELD_3_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|KEYWORD_FIELD_KEY
argument_list|,
name|KEYWORD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|NO_NORMS_KEY
argument_list|,
name|NO_NORMS_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNINDEXED_FIELD_KEY
argument_list|,
name|UNINDEXED_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNSTORED_FIELD_1_KEY
argument_list|,
name|UNSTORED_1_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|UNSTORED_FIELD_2_KEY
argument_list|,
name|UNSTORED_2_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|LAZY_FIELD_KEY
argument_list|,
name|LAZY_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|LAZY_FIELD_BINARY_KEY
argument_list|,
name|LAZY_FIELD_BINARY_BYTES
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|LARGE_LAZY_FIELD_KEY
argument_list|,
name|LARGE_LAZY_FIELD_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_UTF1_KEY
argument_list|,
name|FIELD_UTF1_TEXT
argument_list|)
expr_stmt|;
name|nameValues
operator|.
name|put
argument_list|(
name|TEXT_FIELD_UTF2_KEY
argument_list|,
name|FIELD_UTF2_TEXT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds the fields above to a document     * @param doc The document to write    */
DECL|method|setupDoc
specifier|public
specifier|static
name|void
name|setupDoc
parameter_list|(
name|Document
name|doc
parameter_list|)
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
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Writes the document to the directory using a segment    * named "test"; returns the SegmentInfo describing the new    * segment     * @param dir    * @param doc    * @throws IOException    */
DECL|method|writeDoc
specifier|public
specifier|static
name|SegmentInfo
name|writeDoc
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeDoc
argument_list|(
name|dir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
name|Similarity
operator|.
name|getDefault
argument_list|()
argument_list|,
name|doc
argument_list|)
return|;
block|}
comment|/**    * Writes the document to the directory using the analyzer    * and the similarity score; returns the SegmentInfo    * describing the new segment    * @param dir    * @param analyzer    * @param similarity    * @param doc    * @throws IOException    */
DECL|method|writeDoc
specifier|public
specifier|static
name|SegmentInfo
name|writeDoc
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|Similarity
name|similarity
parameter_list|,
name|Document
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|analyzer
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
argument_list|)
decl_stmt|;
name|writer
operator|.
name|setSimilarity
argument_list|(
name|similarity
argument_list|)
expr_stmt|;
comment|//writer.setUseCompoundFile(false);
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|SegmentInfo
name|info
init|=
name|writer
operator|.
name|newestSegment
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|info
return|;
block|}
DECL|method|numFields
specifier|public
specifier|static
name|int
name|numFields
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
return|return
name|doc
operator|.
name|getFields
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

