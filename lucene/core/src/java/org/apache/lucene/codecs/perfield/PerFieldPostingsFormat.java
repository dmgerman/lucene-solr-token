begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs.perfield
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|perfield
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
name|Closeable
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
name|util
operator|.
name|Collections
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
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|ServiceLoader
import|;
end_import

begin_comment
comment|// javadocs
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|codecs
operator|.
name|PostingsFormat
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
name|Terms
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
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Enables per field postings support.  *<p>  * Note, when extending this class, the name ({@link #getName}) is   * written into the index. In order for the field to be read, the  * name must resolve to your implementation via {@link #forName(String)}.  * This method uses Java's   * {@link ServiceLoader Service Provider Interface} to resolve format names.  *<p>  * Files written by each posting format have an additional suffix containing the   * format name. For example, in a per-field configuration instead of<tt>_1.prx</tt>   * filenames would look like<tt>_1_Lucene40_0.prx</tt>.  * @see ServiceLoader  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PerFieldPostingsFormat
specifier|public
specifier|abstract
class|class
name|PerFieldPostingsFormat
extends|extends
name|PostingsFormat
block|{
comment|/** Name of this {@link PostingsFormat}. */
DECL|field|PER_FIELD_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PER_FIELD_NAME
init|=
literal|"PerField40"
decl_stmt|;
comment|/** {@link FieldInfo} attribute name used to store the    *  format name for each field. */
DECL|field|PER_FIELD_FORMAT_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PER_FIELD_FORMAT_KEY
init|=
name|PerFieldPostingsFormat
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".format"
decl_stmt|;
comment|/** {@link FieldInfo} attribute name used to store the    *  segment suffix name for each field. */
DECL|field|PER_FIELD_SUFFIX_KEY
specifier|public
specifier|static
specifier|final
name|String
name|PER_FIELD_SUFFIX_KEY
init|=
name|PerFieldPostingsFormat
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".suffix"
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|PerFieldPostingsFormat
specifier|public
name|PerFieldPostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
name|PER_FIELD_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
specifier|final
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsWriter
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|class|FieldsConsumerAndSuffix
specifier|static
class|class
name|FieldsConsumerAndSuffix
implements|implements
name|Closeable
block|{
DECL|field|consumer
name|FieldsConsumer
name|consumer
decl_stmt|;
DECL|field|suffix
name|int
name|suffix
decl_stmt|;
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
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|FieldsWriter
specifier|private
class|class
name|FieldsWriter
extends|extends
name|FieldsConsumer
block|{
DECL|field|formats
specifier|private
specifier|final
name|Map
argument_list|<
name|PostingsFormat
argument_list|,
name|FieldsConsumerAndSuffix
argument_list|>
name|formats
init|=
operator|new
name|HashMap
argument_list|<
name|PostingsFormat
argument_list|,
name|FieldsConsumerAndSuffix
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|suffixes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suffixes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|segmentWriteState
specifier|private
specifier|final
name|SegmentWriteState
name|segmentWriteState
decl_stmt|;
DECL|method|FieldsWriter
specifier|public
name|FieldsWriter
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
block|{
name|segmentWriteState
operator|=
name|state
expr_stmt|;
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
throws|throws
name|IOException
block|{
specifier|final
name|PostingsFormat
name|format
init|=
name|getPostingsFormatForField
argument_list|(
name|field
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|format
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"invalid null PostingsFormat for field=\""
operator|+
name|field
operator|.
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
specifier|final
name|String
name|formatName
init|=
name|format
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|previousValue
init|=
name|field
operator|.
name|putAttribute
argument_list|(
name|PER_FIELD_FORMAT_KEY
argument_list|,
name|formatName
argument_list|)
decl_stmt|;
assert|assert
name|previousValue
operator|==
literal|null
assert|;
name|Integer
name|suffix
decl_stmt|;
name|FieldsConsumerAndSuffix
name|consumer
init|=
name|formats
operator|.
name|get
argument_list|(
name|format
argument_list|)
decl_stmt|;
if|if
condition|(
name|consumer
operator|==
literal|null
condition|)
block|{
comment|// First time we are seeing this format; create a new instance
comment|// bump the suffix
name|suffix
operator|=
name|suffixes
operator|.
name|get
argument_list|(
name|formatName
argument_list|)
expr_stmt|;
if|if
condition|(
name|suffix
operator|==
literal|null
condition|)
block|{
name|suffix
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|suffix
operator|=
name|suffix
operator|+
literal|1
expr_stmt|;
block|}
name|suffixes
operator|.
name|put
argument_list|(
name|formatName
argument_list|,
name|suffix
argument_list|)
expr_stmt|;
specifier|final
name|String
name|segmentSuffix
init|=
name|getFullSegmentSuffix
argument_list|(
name|field
operator|.
name|name
argument_list|,
name|segmentWriteState
operator|.
name|segmentSuffix
argument_list|,
name|getSuffix
argument_list|(
name|formatName
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|suffix
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|consumer
operator|=
operator|new
name|FieldsConsumerAndSuffix
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|consumer
operator|=
name|format
operator|.
name|fieldsConsumer
argument_list|(
operator|new
name|SegmentWriteState
argument_list|(
name|segmentWriteState
argument_list|,
name|segmentSuffix
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|suffix
operator|=
name|suffix
expr_stmt|;
name|formats
operator|.
name|put
argument_list|(
name|format
argument_list|,
name|consumer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we've already seen this format, so just grab its suffix
assert|assert
name|suffixes
operator|.
name|containsKey
argument_list|(
name|formatName
argument_list|)
assert|;
name|suffix
operator|=
name|consumer
operator|.
name|suffix
expr_stmt|;
block|}
name|previousValue
operator|=
name|field
operator|.
name|putAttribute
argument_list|(
name|PER_FIELD_SUFFIX_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|suffix
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|previousValue
operator|==
literal|null
assert|;
comment|// TODO: we should only provide the "slice" of FIS
comment|// that this PF actually sees ... then stuff like
comment|// .hasProx could work correctly?
comment|// NOTE: .hasProx is already broken in the same way for the non-perfield case,
comment|// if there is a fieldinfo with prox that has no postings, you get a 0 byte file.
return|return
name|consumer
operator|.
name|consumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
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
comment|// Close all subs
name|IOUtils
operator|.
name|close
argument_list|(
name|formats
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getSuffix
specifier|static
name|String
name|getSuffix
parameter_list|(
name|String
name|formatName
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
name|formatName
operator|+
literal|"_"
operator|+
name|suffix
return|;
block|}
DECL|method|getFullSegmentSuffix
specifier|static
name|String
name|getFullSegmentSuffix
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|outerSegmentSuffix
parameter_list|,
name|String
name|segmentSuffix
parameter_list|)
block|{
if|if
condition|(
name|outerSegmentSuffix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|segmentSuffix
return|;
block|}
else|else
block|{
comment|// TODO: support embedding; I think it should work but
comment|// we need a test confirm to confirm
comment|// return outerSegmentSuffix + "_" + segmentSuffix;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"cannot embed PerFieldPostingsFormat inside itself (field \""
operator|+
name|fieldName
operator|+
literal|"\" returned PerFieldPostingsFormat)"
argument_list|)
throw|;
block|}
block|}
DECL|class|FieldsReader
specifier|private
class|class
name|FieldsReader
extends|extends
name|FieldsProducer
block|{
DECL|field|fields
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldsProducer
argument_list|>
name|fields
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|FieldsProducer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|formats
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldsProducer
argument_list|>
name|formats
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldsProducer
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FieldsReader
specifier|public
name|FieldsReader
parameter_list|(
specifier|final
name|SegmentReadState
name|readState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Read _X.per and init each format:
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|// Read field name -> format name
for|for
control|(
name|FieldInfo
name|fi
range|:
name|readState
operator|.
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fi
operator|.
name|isIndexed
argument_list|()
condition|)
block|{
specifier|final
name|String
name|fieldName
init|=
name|fi
operator|.
name|name
decl_stmt|;
specifier|final
name|String
name|formatName
init|=
name|fi
operator|.
name|getAttribute
argument_list|(
name|PER_FIELD_FORMAT_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|formatName
operator|!=
literal|null
condition|)
block|{
comment|// null formatName means the field is in fieldInfos, but has no postings!
specifier|final
name|String
name|suffix
init|=
name|fi
operator|.
name|getAttribute
argument_list|(
name|PER_FIELD_SUFFIX_KEY
argument_list|)
decl_stmt|;
assert|assert
name|suffix
operator|!=
literal|null
assert|;
name|PostingsFormat
name|format
init|=
name|PostingsFormat
operator|.
name|forName
argument_list|(
name|formatName
argument_list|)
decl_stmt|;
name|String
name|segmentSuffix
init|=
name|getSuffix
argument_list|(
name|formatName
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|formats
operator|.
name|containsKey
argument_list|(
name|segmentSuffix
argument_list|)
condition|)
block|{
name|formats
operator|.
name|put
argument_list|(
name|segmentSuffix
argument_list|,
name|format
operator|.
name|fieldsProducer
argument_list|(
operator|new
name|SegmentReadState
argument_list|(
name|readState
argument_list|,
name|segmentSuffix
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fields
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|formats
operator|.
name|get
argument_list|(
name|segmentSuffix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|formats
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|fields
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terms
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldsProducer
name|fieldsProducer
init|=
name|fields
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
return|return
name|fieldsProducer
operator|==
literal|null
condition|?
literal|null
else|:
name|fieldsProducer
operator|.
name|terms
argument_list|(
name|field
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
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
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|formats
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
specifier|final
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldsReader
argument_list|(
name|state
argument_list|)
return|;
block|}
comment|/**     * Returns the postings format that should be used for writing     * new segments of<code>field</code>.    *<p>    * The field to format mapping is written to the index, so    * this method is only invoked when writing, not when reading. */
DECL|method|getPostingsFormatForField
specifier|public
specifier|abstract
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
function_decl|;
block|}
end_class

end_unit

