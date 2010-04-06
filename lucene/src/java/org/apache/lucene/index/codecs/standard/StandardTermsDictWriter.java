begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.standard
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
name|standard
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|FieldInfo
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
name|FieldInfos
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
name|IndexFileNames
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
name|SegmentWriteState
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
name|BytesRef
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
name|FieldsConsumer
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
name|PostingsConsumer
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
name|TermsConsumer
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
name|CodecUtil
import|;
end_import

begin_comment
comment|/**  * Writes terms dict and interacts with docs/positions  * consumers to write the postings files.  *  * The [new] terms dict format is field-centric: each field  * has its own section in the file.  Fields are written in  * UTF16 string comparison order.  Within each field, each  * term's text is written in UTF16 string comparison order.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|StandardTermsDictWriter
specifier|public
class|class
name|StandardTermsDictWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|CODEC_NAME
specifier|final
specifier|static
name|String
name|CODEC_NAME
init|=
literal|"STANDARD_TERMS_DICT"
decl_stmt|;
comment|// Initial format
DECL|field|VERSION_START
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_START
init|=
literal|0
decl_stmt|;
DECL|field|VERSION_CURRENT
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CURRENT
init|=
name|VERSION_START
decl_stmt|;
DECL|field|termWriter
specifier|private
specifier|final
name|DeltaBytesWriter
name|termWriter
decl_stmt|;
DECL|field|out
specifier|final
name|IndexOutput
name|out
decl_stmt|;
DECL|field|postingsWriter
specifier|final
name|StandardPostingsWriter
name|postingsWriter
decl_stmt|;
DECL|field|fieldInfos
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
DECL|field|currentField
name|FieldInfo
name|currentField
decl_stmt|;
DECL|field|indexWriter
specifier|private
specifier|final
name|StandardTermsIndexWriter
name|indexWriter
decl_stmt|;
DECL|field|fields
specifier|private
specifier|final
name|List
argument_list|<
name|TermsConsumer
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<
name|TermsConsumer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|termComp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
DECL|method|StandardTermsDictWriter
specifier|public
name|StandardTermsDictWriter
parameter_list|(
name|StandardTermsIndexWriter
name|indexWriter
parameter_list|,
name|SegmentWriteState
name|state
parameter_list|,
name|StandardPostingsWriter
name|postingsWriter
parameter_list|,
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|termsFileName
init|=
name|IndexFileNames
operator|.
name|segmentFileName
argument_list|(
name|state
operator|.
name|segmentName
argument_list|,
name|StandardCodec
operator|.
name|TERMS_EXTENSION
argument_list|)
decl_stmt|;
name|this
operator|.
name|indexWriter
operator|=
name|indexWriter
expr_stmt|;
name|this
operator|.
name|termComp
operator|=
name|termComp
expr_stmt|;
name|out
operator|=
name|state
operator|.
name|directory
operator|.
name|createOutput
argument_list|(
name|termsFileName
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|setTermsOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|state
operator|.
name|flushedFiles
operator|.
name|add
argument_list|(
name|termsFileName
argument_list|)
expr_stmt|;
name|fieldInfos
operator|=
name|state
operator|.
name|fieldInfos
expr_stmt|;
comment|// Count indexed fields up front
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|CODEC_NAME
argument_list|,
name|VERSION_CURRENT
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// leave space for end index pointer
name|termWriter
operator|=
operator|new
name|DeltaBytesWriter
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|currentField
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|postingsWriter
operator|=
name|postingsWriter
expr_stmt|;
name|postingsWriter
operator|.
name|start
argument_list|(
name|out
argument_list|)
expr_stmt|;
comment|// have consumer write its format/header
block|}
annotation|@
name|Override
DECL|method|addField
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
block|{
assert|assert
name|currentField
operator|==
literal|null
operator|||
name|currentField
operator|.
name|name
operator|.
name|compareTo
argument_list|(
name|field
operator|.
name|name
argument_list|)
operator|<
literal|0
assert|;
name|currentField
operator|=
name|field
expr_stmt|;
name|StandardTermsIndexWriter
operator|.
name|FieldWriter
name|fieldIndexWriter
init|=
name|indexWriter
operator|.
name|addField
argument_list|(
name|field
argument_list|)
decl_stmt|;
name|TermsConsumer
name|terms
init|=
operator|new
name|TermsWriter
argument_list|(
name|fieldIndexWriter
argument_list|,
name|field
argument_list|,
name|postingsWriter
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|terms
argument_list|)
expr_stmt|;
return|return
name|terms
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
specifier|final
name|int
name|fieldCount
init|=
name|fields
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|long
name|dirStart
init|=
name|out
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|fieldCount
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|TermsWriter
name|field
init|=
operator|(
name|TermsWriter
operator|)
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|field
operator|.
name|fieldInfo
operator|.
name|number
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|field
operator|.
name|numTerms
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|field
operator|.
name|termsStartPointer
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|seek
argument_list|(
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|CODEC_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|dirStart
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|TermsWriter
class|class
name|TermsWriter
extends|extends
name|TermsConsumer
block|{
DECL|field|fieldInfo
specifier|private
specifier|final
name|FieldInfo
name|fieldInfo
decl_stmt|;
DECL|field|postingsWriter
specifier|private
specifier|final
name|StandardPostingsWriter
name|postingsWriter
decl_stmt|;
DECL|field|termsStartPointer
specifier|private
specifier|final
name|long
name|termsStartPointer
decl_stmt|;
DECL|field|numTerms
specifier|private
name|long
name|numTerms
decl_stmt|;
DECL|field|fieldIndexWriter
specifier|private
specifier|final
name|StandardTermsIndexWriter
operator|.
name|FieldWriter
name|fieldIndexWriter
decl_stmt|;
DECL|method|TermsWriter
name|TermsWriter
parameter_list|(
name|StandardTermsIndexWriter
operator|.
name|FieldWriter
name|fieldIndexWriter
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|StandardPostingsWriter
name|postingsWriter
parameter_list|)
block|{
name|this
operator|.
name|fieldInfo
operator|=
name|fieldInfo
expr_stmt|;
name|this
operator|.
name|fieldIndexWriter
operator|=
name|fieldIndexWriter
expr_stmt|;
name|termWriter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|termsStartPointer
operator|=
name|out
operator|.
name|getFilePointer
argument_list|()
expr_stmt|;
name|postingsWriter
operator|.
name|setField
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|postingsWriter
operator|=
name|postingsWriter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|termComp
return|;
block|}
annotation|@
name|Override
DECL|method|startTerm
specifier|public
name|PostingsConsumer
name|startTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|postingsWriter
operator|.
name|startTerm
argument_list|()
expr_stmt|;
return|return
name|postingsWriter
return|;
block|}
annotation|@
name|Override
DECL|method|finishTerm
specifier|public
name|void
name|finishTerm
parameter_list|(
name|BytesRef
name|text
parameter_list|,
name|int
name|numDocs
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|numDocs
operator|>
literal|0
assert|;
specifier|final
name|boolean
name|isIndexTerm
init|=
name|fieldIndexWriter
operator|.
name|checkIndexTerm
argument_list|(
name|text
argument_list|,
name|numDocs
argument_list|)
decl_stmt|;
name|termWriter
operator|.
name|write
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|numDocs
argument_list|)
expr_stmt|;
name|postingsWriter
operator|.
name|finishTerm
argument_list|(
name|numDocs
argument_list|,
name|isIndexTerm
argument_list|)
expr_stmt|;
name|numTerms
operator|++
expr_stmt|;
block|}
comment|// Finishes all terms in this field
annotation|@
name|Override
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldIndexWriter
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

