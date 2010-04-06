begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs.sep
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
name|sep
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
name|Set
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
name|SegmentInfo
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
name|index
operator|.
name|SegmentReadState
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
name|Codec
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
name|FieldsProducer
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
name|standard
operator|.
name|SimpleStandardTermsIndexReader
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
name|standard
operator|.
name|SimpleStandardTermsIndexWriter
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
name|standard
operator|.
name|StandardPostingsReader
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
name|standard
operator|.
name|StandardPostingsWriter
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
name|standard
operator|.
name|StandardTermsDictReader
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
name|standard
operator|.
name|StandardTermsDictWriter
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
name|standard
operator|.
name|StandardTermsIndexReader
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
name|standard
operator|.
name|StandardTermsIndexWriter
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
name|standard
operator|.
name|StandardCodec
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_comment
comment|/** @lucene.experimental */
end_comment

begin_class
DECL|class|SepCodec
specifier|public
class|class
name|SepCodec
extends|extends
name|Codec
block|{
DECL|method|SepCodec
specifier|public
name|SepCodec
parameter_list|()
block|{
name|name
operator|=
literal|"Sep"
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|StandardPostingsWriter
name|postingsWriter
init|=
operator|new
name|SepPostingsWriterImpl
argument_list|(
name|state
argument_list|,
operator|new
name|SingleIntFactory
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|StandardTermsIndexWriter
name|indexWriter
decl_stmt|;
try|try
block|{
name|indexWriter
operator|=
operator|new
name|SimpleStandardTermsIndexWriter
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|FieldsConsumer
name|ret
init|=
operator|new
name|StandardTermsDictWriter
argument_list|(
name|indexWriter
argument_list|,
name|state
argument_list|,
name|postingsWriter
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
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
DECL|field|DOC_EXTENSION
specifier|final
specifier|static
name|String
name|DOC_EXTENSION
init|=
literal|"doc"
decl_stmt|;
DECL|field|SKIP_EXTENSION
specifier|final
specifier|static
name|String
name|SKIP_EXTENSION
init|=
literal|"skp"
decl_stmt|;
DECL|field|FREQ_EXTENSION
specifier|final
specifier|static
name|String
name|FREQ_EXTENSION
init|=
literal|"frq"
decl_stmt|;
DECL|field|POS_EXTENSION
specifier|final
specifier|static
name|String
name|POS_EXTENSION
init|=
literal|"pos"
decl_stmt|;
DECL|field|PAYLOAD_EXTENSION
specifier|final
specifier|static
name|String
name|PAYLOAD_EXTENSION
init|=
literal|"pyl"
decl_stmt|;
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|StandardPostingsReader
name|postingsReader
init|=
operator|new
name|SepPostingsReaderImpl
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|segmentInfo
argument_list|,
name|state
operator|.
name|readBufferSize
argument_list|,
operator|new
name|SingleIntFactory
argument_list|()
argument_list|)
decl_stmt|;
name|StandardTermsIndexReader
name|indexReader
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|indexReader
operator|=
operator|new
name|SimpleStandardTermsIndexReader
argument_list|(
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|state
operator|.
name|termsIndexDivisor
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|FieldsProducer
name|ret
init|=
operator|new
name|StandardTermsDictReader
argument_list|(
name|indexReader
argument_list|,
name|state
operator|.
name|dir
argument_list|,
name|state
operator|.
name|fieldInfos
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|name
argument_list|,
name|postingsReader
argument_list|,
name|state
operator|.
name|readBufferSize
argument_list|,
name|BytesRef
operator|.
name|getUTF8SortedAsUTF16Comparator
argument_list|()
argument_list|,
name|StandardCodec
operator|.
name|TERMS_CACHE_SIZE
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|ret
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
try|try
block|{
name|postingsReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|files
specifier|public
name|void
name|files
parameter_list|(
name|Directory
name|dir
parameter_list|,
name|SegmentInfo
name|segmentInfo
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|files
parameter_list|)
block|{
name|SepPostingsReaderImpl
operator|.
name|files
argument_list|(
name|segmentInfo
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|StandardTermsDictReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|files
argument_list|)
expr_stmt|;
name|SimpleStandardTermsIndexReader
operator|.
name|files
argument_list|(
name|dir
argument_list|,
name|segmentInfo
argument_list|,
name|files
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExtensions
specifier|public
name|void
name|getExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|getSepExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
block|}
DECL|method|getSepExtensions
specifier|public
specifier|static
name|void
name|getSepExtensions
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|extensions
parameter_list|)
block|{
name|extensions
operator|.
name|add
argument_list|(
name|DOC_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|FREQ_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|SKIP_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|POS_EXTENSION
argument_list|)
expr_stmt|;
name|extensions
operator|.
name|add
argument_list|(
name|PAYLOAD_EXTENSION
argument_list|)
expr_stmt|;
name|StandardTermsDictReader
operator|.
name|getExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
name|SimpleStandardTermsIndexReader
operator|.
name|getIndexExtensions
argument_list|(
name|extensions
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

