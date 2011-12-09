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
name|Collection
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
name|DocValues
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
name|DocValues
operator|.
name|Type
import|;
end_import

begin_comment
comment|// javadocs
end_comment

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
name|PerDocValues
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
name|lucene40
operator|.
name|values
operator|.
name|Bytes
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
name|lucene40
operator|.
name|values
operator|.
name|Floats
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
name|lucene40
operator|.
name|values
operator|.
name|Ints
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
name|store
operator|.
name|IOContext
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
comment|/**  * Abstract base class for PerDocValues implementations  * @lucene.experimental  */
end_comment

begin_comment
comment|// TODO: this needs to go under lucene40 codec (its specific to its impl)
end_comment

begin_class
DECL|class|DocValuesReaderBase
specifier|public
specifier|abstract
class|class
name|DocValuesReaderBase
extends|extends
name|PerDocValues
block|{
DECL|method|closeInternal
specifier|protected
specifier|abstract
name|void
name|closeInternal
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Closeable
argument_list|>
name|closeables
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|docValues
specifier|protected
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|docValues
parameter_list|()
function_decl|;
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
name|closeInternal
argument_list|(
name|docValues
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|docValues
specifier|public
name|DocValues
name|docValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|docValues
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
comment|// Only opens files... doesn't actually load any values
DECL|method|load
specifier|protected
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|load
parameter_list|(
name|FieldInfos
name|fieldInfos
parameter_list|,
name|String
name|segment
parameter_list|,
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
name|values
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|DocValues
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|fieldInfos
control|)
block|{
if|if
condition|(
name|fieldInfo
operator|.
name|hasDocValues
argument_list|()
condition|)
block|{
specifier|final
name|String
name|field
init|=
name|fieldInfo
operator|.
name|name
decl_stmt|;
comment|// TODO can we have a compound file per segment and codec for
comment|// docvalues?
specifier|final
name|String
name|id
init|=
name|DocValuesWriterBase
operator|.
name|docValuesId
argument_list|(
name|segment
argument_list|,
name|fieldInfo
operator|.
name|number
argument_list|)
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|loadDocValues
argument_list|(
name|docCount
argument_list|,
name|dir
argument_list|,
name|id
argument_list|,
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
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
comment|// if we fail we must close all opened resources if there are any
name|closeInternal
argument_list|(
name|values
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|values
return|;
block|}
comment|/**    * Loads a {@link DocValues} instance depending on the given {@link Type}.    * Codecs that use different implementations for a certain {@link Type} can    * simply override this method and return their custom implementations.    *     * @param docCount    *          number of documents in the segment    * @param dir    *          the {@link Directory} to load the {@link DocValues} from    * @param id    *          the unique file ID within the segment    * @param type    *          the type to load    * @return a {@link DocValues} instance for the given type    * @throws IOException    *           if an {@link IOException} occurs    * @throws IllegalArgumentException    *           if the given {@link Type} is not supported    */
DECL|method|loadDocValues
specifier|protected
name|DocValues
name|loadDocValues
parameter_list|(
name|int
name|docCount
parameter_list|,
name|Directory
name|dir
parameter_list|,
name|String
name|id
parameter_list|,
name|DocValues
operator|.
name|Type
name|type
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|FIXED_INTS_16
case|:
case|case
name|FIXED_INTS_32
case|:
case|case
name|FIXED_INTS_64
case|:
case|case
name|FIXED_INTS_8
case|:
case|case
name|VAR_INTS
case|:
return|return
name|Ints
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|,
name|type
argument_list|,
name|context
argument_list|)
return|;
case|case
name|FLOAT_32
case|:
return|return
name|Floats
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
case|case
name|FLOAT_64
case|:
return|return
name|Floats
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|docCount
argument_list|,
name|context
argument_list|,
name|type
argument_list|)
return|;
case|case
name|BYTES_FIXED_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_FIXED_DEREF
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_FIXED_SORTED
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|true
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_VAR_STRAIGHT
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|STRAIGHT
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_VAR_DEREF
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|DEREF
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
case|case
name|BYTES_VAR_SORTED
case|:
return|return
name|Bytes
operator|.
name|getValues
argument_list|(
name|dir
argument_list|,
name|id
argument_list|,
name|Bytes
operator|.
name|Mode
operator|.
name|SORTED
argument_list|,
literal|false
argument_list|,
name|docCount
argument_list|,
name|getComparator
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unrecognized index values mode "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

