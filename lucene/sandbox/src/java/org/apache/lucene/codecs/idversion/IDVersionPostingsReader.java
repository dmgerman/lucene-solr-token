begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.idversion
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|idversion
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|BlockTermState
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
name|codecs
operator|.
name|CodecUtil
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
name|codecs
operator|.
name|PostingsReaderBase
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
name|PostingsEnum
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
name|store
operator|.
name|DataInput
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
name|util
operator|.
name|Bits
import|;
end_import

begin_class
DECL|class|IDVersionPostingsReader
specifier|final
class|class
name|IDVersionPostingsReader
extends|extends
name|PostingsReaderBase
block|{
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|IndexInput
name|termsIn
parameter_list|,
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make sure we are talking to the matching postings writer
name|CodecUtil
operator|.
name|checkIndexHeader
argument_list|(
name|termsIn
argument_list|,
name|IDVersionPostingsWriter
operator|.
name|TERMS_CODEC
argument_list|,
name|IDVersionPostingsWriter
operator|.
name|VERSION_START
argument_list|,
name|IDVersionPostingsWriter
operator|.
name|VERSION_CURRENT
argument_list|,
name|state
operator|.
name|segmentInfo
operator|.
name|getId
argument_list|()
argument_list|,
name|state
operator|.
name|segmentSuffix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newTermState
specifier|public
name|BlockTermState
name|newTermState
parameter_list|()
block|{
return|return
operator|new
name|IDVersionTermState
argument_list|()
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
block|{   }
annotation|@
name|Override
DECL|method|decodeTerm
specifier|public
name|void
name|decodeTerm
parameter_list|(
name|long
index|[]
name|longs
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|_termState
parameter_list|,
name|boolean
name|absolute
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IDVersionTermState
name|termState
init|=
operator|(
name|IDVersionTermState
operator|)
name|_termState
decl_stmt|;
name|termState
operator|.
name|docID
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|absolute
condition|)
block|{
name|termState
operator|.
name|idVersion
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|termState
operator|.
name|idVersion
operator|+=
name|in
operator|.
name|readZLong
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postings
specifier|public
name|PostingsEnum
name|postings
parameter_list|(
name|FieldInfo
name|fieldInfo
parameter_list|,
name|BlockTermState
name|termState
parameter_list|,
name|PostingsEnum
name|reuse
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|SingleDocsEnum
name|docsEnum
decl_stmt|;
if|if
condition|(
name|PostingsEnum
operator|.
name|featureRequested
argument_list|(
name|flags
argument_list|,
name|PostingsEnum
operator|.
name|POSITIONS
argument_list|)
condition|)
block|{
name|SinglePostingsEnum
name|posEnum
decl_stmt|;
if|if
condition|(
name|reuse
operator|instanceof
name|SinglePostingsEnum
condition|)
block|{
name|posEnum
operator|=
operator|(
name|SinglePostingsEnum
operator|)
name|reuse
expr_stmt|;
block|}
else|else
block|{
name|posEnum
operator|=
operator|new
name|SinglePostingsEnum
argument_list|()
expr_stmt|;
block|}
name|IDVersionTermState
name|_termState
init|=
operator|(
name|IDVersionTermState
operator|)
name|termState
decl_stmt|;
name|posEnum
operator|.
name|reset
argument_list|(
name|_termState
operator|.
name|docID
argument_list|,
name|_termState
operator|.
name|idVersion
argument_list|)
expr_stmt|;
return|return
name|posEnum
return|;
block|}
if|if
condition|(
name|reuse
operator|instanceof
name|SingleDocsEnum
condition|)
block|{
name|docsEnum
operator|=
operator|(
name|SingleDocsEnum
operator|)
name|reuse
expr_stmt|;
block|}
else|else
block|{
name|docsEnum
operator|=
operator|new
name|SingleDocsEnum
argument_list|()
expr_stmt|;
block|}
name|docsEnum
operator|.
name|reset
argument_list|(
operator|(
operator|(
name|IDVersionTermState
operator|)
name|termState
operator|)
operator|.
name|docID
argument_list|)
expr_stmt|;
return|return
name|docsEnum
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|checkIntegrity
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

