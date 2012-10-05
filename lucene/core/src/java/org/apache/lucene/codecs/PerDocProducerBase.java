begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.codecs
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
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
comment|/**  * Abstract base class for PerDocProducer implementations  * @lucene.experimental  */
end_comment

begin_class
DECL|class|PerDocProducerBase
specifier|public
specifier|abstract
class|class
name|PerDocProducerBase
extends|extends
name|PerDocProducer
block|{
comment|/** Closes provided Closables. */
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
comment|/** Returns a map, mapping field names to doc values. */
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
comment|/** Sole constructor. (For invocation by subclass     *  constructors, typically implicit.) */
DECL|method|PerDocProducerBase
specifier|protected
name|PerDocProducerBase
parameter_list|()
block|{   }
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
comment|/** Returns the comparator used to sort {@link BytesRef} values. */
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
comment|/** Only opens files... doesn't actually load any values. */
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
name|canLoad
argument_list|(
name|fieldInfo
argument_list|)
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
specifier|final
name|String
name|id
init|=
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
name|getDocValuesType
argument_list|(
name|fieldInfo
argument_list|)
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
try|try
block|{
name|closeInternal
argument_list|(
name|values
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{}
comment|// keep our original exception
block|}
block|}
return|return
name|values
return|;
block|}
comment|/** Returns true if this field indexed doc values. */
DECL|method|canLoad
specifier|protected
name|boolean
name|canLoad
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|hasDocValues
argument_list|()
return|;
block|}
comment|/** Returns the doc values type for this field. */
DECL|method|getDocValuesType
specifier|protected
name|Type
name|getDocValuesType
parameter_list|(
name|FieldInfo
name|info
parameter_list|)
block|{
return|return
name|info
operator|.
name|getDocValuesType
argument_list|()
return|;
block|}
comment|/** Returns true if any fields indexed doc values. */
DECL|method|anyDocValuesFields
specifier|protected
name|boolean
name|anyDocValuesFields
parameter_list|(
name|FieldInfos
name|infos
parameter_list|)
block|{
return|return
name|infos
operator|.
name|hasDocValues
argument_list|()
return|;
block|}
comment|/** Returns the unique segment and field id for any    *  per-field files this implementation needs to write. */
DECL|method|docValuesId
specifier|public
specifier|static
name|String
name|docValuesId
parameter_list|(
name|String
name|segmentsName
parameter_list|,
name|int
name|fieldId
parameter_list|)
block|{
return|return
name|segmentsName
operator|+
literal|"_"
operator|+
name|fieldId
return|;
block|}
comment|/**    * Loads a {@link DocValues} instance depending on the given {@link Type}.    * Codecs that use different implementations for a certain {@link Type} can    * simply override this method and return their custom implementations.    *     * @param docCount    *          number of documents in the segment    * @param dir    *          the {@link Directory} to load the {@link DocValues} from    * @param id    *          the unique file ID within the segment    * @param type    *          the type to load    * @return a {@link DocValues} instance for the given type    * @throws IOException    *           if an {@link IOException} occurs    * @throws IllegalArgumentException    *           if the given {@link Type} is not supported    */
DECL|method|loadDocValues
specifier|protected
specifier|abstract
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
function_decl|;
block|}
end_class

end_unit

