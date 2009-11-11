begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
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
name|FieldCache
import|;
end_import

begin_comment
comment|/**  * Expert: A base class for ValueSource implementations that retrieve values for  * a single field from the {@link org.apache.lucene.search.FieldCache FieldCache}.  *<p>  * Fields used herein must be indexed (doesn't matter if these fields are stored or not).  *<p>   * It is assumed that each such indexed field is untokenized, or at least has a single token in a document.  * For documents with multiple tokens of the same field, behavior is undefined (It is likely that current   * code would use the value of one of these tokens, but this is not guaranteed).  *<p>  * Document with no tokens in this field are assigned the<code>Zero</code> value.      *   *<p><font color="#FF0000">  * WARNING: The status of the<b>search.function</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *  *<p><b>NOTE</b>: with the switch in 2.9 to segment-based  * searching, if {@link #getValues} is invoked with a  * composite (multi-segment) reader, this can easily cause  * double RAM usage for the values in the FieldCache.  It's  * best to switch your application to pass only atomic  * (single segment) readers to this API.</p>  */
end_comment

begin_class
DECL|class|FieldCacheSource
specifier|public
specifier|abstract
class|class
name|FieldCacheSource
extends|extends
name|ValueSource
block|{
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
comment|/**    * Create a cached field source for the input field.      */
DECL|method|FieldCacheSource
specifier|public
name|FieldCacheSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
comment|/* (non-Javadoc) @see org.apache.lucene.search.function.ValueSource#getValues(org.apache.lucene.index.IndexReader) */
annotation|@
name|Override
DECL|method|getValues
specifier|public
specifier|final
name|DocValues
name|getValues
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getCachedFieldValues
argument_list|(
name|FieldCache
operator|.
name|DEFAULT
argument_list|,
name|field
argument_list|,
name|reader
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) @see org.apache.lucene.search.function.ValueSource#description() */
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|field
return|;
block|}
comment|/**    * Return cached DocValues for input field and reader.    * @param cache FieldCache so that values of a field are loaded once per reader (RAM allowing)    * @param field Field for which values are required.    * @see ValueSource    */
DECL|method|getCachedFieldValues
specifier|public
specifier|abstract
name|DocValues
name|getCachedFieldValues
parameter_list|(
name|FieldCache
name|cache
parameter_list|,
name|String
name|field
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/*(non-Javadoc) @see java.lang.Object#equals(java.lang.Object) */
annotation|@
name|Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|FieldCacheSource
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FieldCacheSource
name|other
init|=
operator|(
name|FieldCacheSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|other
operator|.
name|field
argument_list|)
operator|&&
name|cachedFieldSourceEquals
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/*(non-Javadoc) @see java.lang.Object#hashCode() */
annotation|@
name|Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|field
operator|.
name|hashCode
argument_list|()
operator|+
name|cachedFieldSourceHashCode
argument_list|()
return|;
block|}
comment|/**    * Check if equals to another {@link FieldCacheSource}, already knowing that cache and field are equal.      * @see Object#equals(java.lang.Object)    */
DECL|method|cachedFieldSourceEquals
specifier|public
specifier|abstract
name|boolean
name|cachedFieldSourceEquals
parameter_list|(
name|FieldCacheSource
name|other
parameter_list|)
function_decl|;
comment|/**    * Return a hash code of a {@link FieldCacheSource}, without the hash-codes of the field     * and the cache (those are taken care of elsewhere).      * @see Object#hashCode()    */
DECL|method|cachedFieldSourceHashCode
specifier|public
specifier|abstract
name|int
name|cachedFieldSourceHashCode
parameter_list|()
function_decl|;
block|}
end_class

end_unit

