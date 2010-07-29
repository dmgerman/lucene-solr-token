begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index.codecs
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
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|HashSet
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
name|codecs
operator|.
name|intblock
operator|.
name|IntBlockCodec
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
name|preflex
operator|.
name|PreFlexCodec
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
name|pulsing
operator|.
name|PulsingCodec
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
name|sep
operator|.
name|SepCodec
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

begin_comment
comment|/** Holds a set of codecs, keyed by name.  You subclass  *  this, instantiate it, and register your codecs, then  *  pass this instance to IndexReader/IndexWriter (via  *  package private APIs) to use different codecs when  *  reading& writing segments.   *  *  @lucene.experimental */
end_comment

begin_class
DECL|class|CodecProvider
specifier|public
specifier|abstract
class|class
name|CodecProvider
block|{
DECL|field|infosWriter
specifier|private
name|SegmentInfosWriter
name|infosWriter
init|=
operator|new
name|DefaultSegmentInfosWriter
argument_list|()
decl_stmt|;
DECL|field|infosReader
specifier|private
name|SegmentInfosReader
name|infosReader
init|=
operator|new
name|DefaultSegmentInfosReader
argument_list|()
decl_stmt|;
DECL|field|codecs
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|Codec
argument_list|>
name|codecs
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Codec
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|knownExtensions
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|knownExtensions
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|defaultCodec
specifier|private
specifier|static
name|String
name|defaultCodec
init|=
literal|"Standard"
decl_stmt|;
DECL|field|CORE_CODECS
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|CORE_CODECS
init|=
operator|new
name|String
index|[]
block|{
literal|"Standard"
block|,
literal|"Sep"
block|,
literal|"Pulsing"
block|,
literal|"IntBlock"
block|,
literal|"PreFlex"
block|}
decl_stmt|;
DECL|method|register
specifier|public
specifier|synchronized
name|void
name|register
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
if|if
condition|(
name|codec
operator|.
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"code.name is null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|codecs
operator|.
name|containsKey
argument_list|(
name|codec
operator|.
name|name
argument_list|)
condition|)
block|{
name|codecs
operator|.
name|put
argument_list|(
name|codec
operator|.
name|name
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|codec
operator|.
name|getExtensions
argument_list|(
name|knownExtensions
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|codecs
operator|.
name|get
argument_list|(
name|codec
operator|.
name|name
argument_list|)
operator|!=
name|codec
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec '"
operator|+
name|codec
operator|.
name|name
operator|+
literal|"' is already registered as a different codec instance"
argument_list|)
throw|;
block|}
block|}
comment|/** @lucene.internal */
DECL|method|unregister
specifier|public
specifier|synchronized
name|void
name|unregister
parameter_list|(
name|Codec
name|codec
parameter_list|)
block|{
if|if
condition|(
name|codec
operator|.
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"code.name is null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|codecs
operator|.
name|containsKey
argument_list|(
name|codec
operator|.
name|name
argument_list|)
condition|)
block|{
name|Codec
name|c
init|=
name|codecs
operator|.
name|get
argument_list|(
name|codec
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
name|c
condition|)
block|{
name|codecs
operator|.
name|remove
argument_list|(
name|codec
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"codec '"
operator|+
name|codec
operator|.
name|name
operator|+
literal|"' is being impersonated by a different codec instance!!!"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|getAllExtensions
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getAllExtensions
parameter_list|()
block|{
return|return
name|knownExtensions
return|;
block|}
DECL|method|lookup
specifier|public
specifier|synchronized
name|Codec
name|lookup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Codec
name|codec
init|=
operator|(
name|Codec
operator|)
name|codecs
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"required codec '"
operator|+
name|name
operator|+
literal|"' not found"
argument_list|)
throw|;
return|return
name|codec
return|;
block|}
DECL|method|getWriter
specifier|public
specifier|abstract
name|Codec
name|getWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
function_decl|;
DECL|method|getSegmentInfosWriter
specifier|public
name|SegmentInfosWriter
name|getSegmentInfosWriter
parameter_list|()
block|{
return|return
name|infosWriter
return|;
block|}
DECL|method|getSegmentInfosReader
specifier|public
name|SegmentInfosReader
name|getSegmentInfosReader
parameter_list|()
block|{
return|return
name|infosReader
return|;
block|}
DECL|field|defaultCodecs
specifier|static
specifier|private
specifier|final
name|CodecProvider
name|defaultCodecs
init|=
operator|new
name|DefaultCodecProvider
argument_list|()
decl_stmt|;
DECL|method|getDefault
specifier|public
specifier|static
name|CodecProvider
name|getDefault
parameter_list|()
block|{
return|return
name|defaultCodecs
return|;
block|}
comment|/** Used for testing. @lucene.internal */
DECL|method|setDefaultCodec
specifier|public
specifier|synchronized
specifier|static
name|void
name|setDefaultCodec
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|defaultCodec
operator|=
name|s
expr_stmt|;
block|}
comment|/** Used for testing. @lucene.internal */
DECL|method|getDefaultCodec
specifier|public
specifier|synchronized
specifier|static
name|String
name|getDefaultCodec
parameter_list|()
block|{
return|return
name|defaultCodec
return|;
block|}
block|}
end_class

begin_class
DECL|class|DefaultCodecProvider
class|class
name|DefaultCodecProvider
extends|extends
name|CodecProvider
block|{
DECL|method|DefaultCodecProvider
name|DefaultCodecProvider
parameter_list|()
block|{
name|register
argument_list|(
operator|new
name|StandardCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|IntBlockCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PreFlexCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|PulsingCodec
argument_list|()
argument_list|)
expr_stmt|;
name|register
argument_list|(
operator|new
name|SepCodec
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriter
specifier|public
name|Codec
name|getWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
block|{
return|return
name|lookup
argument_list|(
name|CodecProvider
operator|.
name|getDefaultCodec
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

