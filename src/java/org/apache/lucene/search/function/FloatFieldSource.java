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
name|function
operator|.
name|DocValues
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

begin_comment
comment|/**  * Expert: obtains float field values from the   * {@link org.apache.lucene.search.FieldCache FieldCache}  * using<code>getFloats()</code> and makes those values   * available as other numeric types, casting as needed.  *   *<p><font color="#FF0000">  * WARNING: The status of the<b>search.function</b> package is experimental.   * The APIs introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *   * @see org.apache.lucene.search.function.FieldCacheSource for requirements   * on the field.  *    * @author yonik  */
end_comment

begin_class
DECL|class|FloatFieldSource
specifier|public
class|class
name|FloatFieldSource
extends|extends
name|FieldCacheSource
block|{
DECL|field|parser
specifier|private
name|FieldCache
operator|.
name|FloatParser
name|parser
decl_stmt|;
comment|/**    * Create a cached float field source with default string-to-float parser.     */
DECL|method|FloatFieldSource
specifier|public
name|FloatFieldSource
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a cached float field source with a specific string-to-float parser.     */
DECL|method|FloatFieldSource
specifier|public
name|FloatFieldSource
parameter_list|(
name|String
name|field
parameter_list|,
name|FieldCache
operator|.
name|FloatParser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.ValueSource#description() */
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"float("
operator|+
name|super
operator|.
name|description
argument_list|()
operator|+
literal|')'
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.FieldCacheSource#getCachedValues(org.apache.lucene.search.FieldCache, java.lang.String, org.apache.lucene.index.IndexReader) */
DECL|method|getCachedFieldValues
specifier|public
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
block|{
specifier|final
name|float
index|[]
name|arr
init|=
operator|(
name|parser
operator|==
literal|null
operator|)
condition|?
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
else|:
name|cache
operator|.
name|getFloats
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|parser
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocValues
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
block|{
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#floatVal(int) */
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|arr
index|[
name|doc
index|]
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#toString(int) */
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|description
argument_list|()
operator|+
literal|'='
operator|+
name|arr
index|[
name|doc
index|]
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.DocValues#getInnerArray() */
name|Object
name|getInnerArray
parameter_list|()
block|{
return|return
name|arr
return|;
block|}
block|}
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.FieldCacheSource#cachedFieldSourceEquals(org.apache.lucene.search.function.FieldCacheSource) */
DECL|method|cachedFieldSourceEquals
specifier|public
name|boolean
name|cachedFieldSourceEquals
parameter_list|(
name|FieldCacheSource
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|.
name|getClass
argument_list|()
operator|!=
name|FloatFieldSource
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FloatFieldSource
name|other
init|=
operator|(
name|FloatFieldSource
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|parser
operator|==
literal|null
condition|?
name|other
operator|.
name|parser
operator|==
literal|null
else|:
name|this
operator|.
name|parser
operator|.
name|getClass
argument_list|()
operator|==
name|other
operator|.
name|parser
operator|.
name|getClass
argument_list|()
return|;
block|}
comment|/*(non-Javadoc) @see org.apache.lucene.search.function.FieldCacheSource#cachedFieldSourceHashCode() */
DECL|method|cachedFieldSourceHashCode
specifier|public
name|int
name|cachedFieldSourceHashCode
parameter_list|()
block|{
return|return
name|parser
operator|==
literal|null
condition|?
name|Float
operator|.
name|class
operator|.
name|hashCode
argument_list|()
else|:
name|parser
operator|.
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

