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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_comment
comment|/**  *  Access to the Field Info file that describes document fields and whether or  *  not they are indexed. Each segment has a separate Field Info file. Objects  *  of this class are thread-safe for multiple readers, but only one thread can  *  be adding documents at a time, with no other reader or writer threads  *  accessing this object.  **/
end_comment

begin_class
DECL|class|FieldInfo
specifier|public
specifier|final
class|class
name|FieldInfo
block|{
comment|/** Field's name */
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Internal field number */
DECL|field|number
specifier|public
specifier|final
name|int
name|number
decl_stmt|;
DECL|field|docValuesType
specifier|private
name|DocValuesType
name|docValuesType
init|=
name|DocValuesType
operator|.
name|NONE
decl_stmt|;
comment|// True if any document indexed term vectors
DECL|field|storeTermVector
specifier|private
name|boolean
name|storeTermVector
decl_stmt|;
DECL|field|omitNorms
specifier|private
name|boolean
name|omitNorms
decl_stmt|;
comment|// omit norms associated with indexed fields
DECL|field|indexOptions
specifier|private
name|IndexOptions
name|indexOptions
init|=
name|IndexOptions
operator|.
name|NONE
decl_stmt|;
DECL|field|storePayloads
specifier|private
name|boolean
name|storePayloads
decl_stmt|;
comment|// whether this field stores payloads together with term positions
DECL|field|attributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|field|dvGen
specifier|private
name|long
name|dvGen
decl_stmt|;
comment|/**    * Sole constructor.    *    * @lucene.experimental    */
DECL|method|FieldInfo
specifier|public
name|FieldInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|number
parameter_list|,
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|,
name|DocValuesType
name|docValues
parameter_list|,
name|long
name|dvGen
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|docValues
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"DocValuesType cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|indexOptions
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"IndexOptions cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|number
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|docValuesType
operator|=
name|docValues
expr_stmt|;
name|this
operator|.
name|indexOptions
operator|=
name|indexOptions
expr_stmt|;
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|this
operator|.
name|storeTermVector
operator|=
name|storeTermVector
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
name|storePayloads
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
block|}
else|else
block|{
comment|// for non-indexed fields, leave defaults
name|this
operator|.
name|storeTermVector
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
literal|false
expr_stmt|;
block|}
name|this
operator|.
name|dvGen
operator|=
name|dvGen
expr_stmt|;
name|this
operator|.
name|attributes
operator|=
name|attributes
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/**     * Performs internal consistency checks.    * Always returns true (or throws IllegalStateException)     */
DECL|method|checkConsistency
specifier|public
name|boolean
name|checkConsistency
parameter_list|()
block|{
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// Cannot store payloads unless positions are indexed:
if|if
condition|(
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|<
literal|0
operator|&&
name|storePayloads
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"indexed field '"
operator|+
name|name
operator|+
literal|"' cannot have payloads without positions"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|storeTermVector
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"non-indexed field '"
operator|+
name|name
operator|+
literal|"' cannot store term vectors"
argument_list|)
throw|;
block|}
if|if
condition|(
name|storePayloads
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"non-indexed field '"
operator|+
name|name
operator|+
literal|"' cannot store payloads"
argument_list|)
throw|;
block|}
if|if
condition|(
name|omitNorms
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"non-indexed field '"
operator|+
name|name
operator|+
literal|"' cannot omit norms"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|dvGen
operator|!=
operator|-
literal|1
operator|&&
name|docValuesType
operator|==
name|DocValuesType
operator|.
name|NONE
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"field '"
operator|+
name|name
operator|+
literal|"' cannot have a docvalues update generation without having docvalues"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|update
name|void
name|update
parameter_list|(
name|IndexableFieldType
name|ft
parameter_list|)
block|{
name|update
argument_list|(
literal|false
argument_list|,
name|ft
operator|.
name|omitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|ft
operator|.
name|indexOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// should only be called by FieldInfos#addOrUpdate
DECL|method|update
name|void
name|update
parameter_list|(
name|boolean
name|storeTermVector
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|storePayloads
parameter_list|,
name|IndexOptions
name|indexOptions
parameter_list|)
block|{
if|if
condition|(
name|indexOptions
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"IndexOptions cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
comment|//System.out.println("FI.update field=" + name + " indexed=" + indexed + " omitNorms=" + omitNorms + " this.omitNorms=" + this.omitNorms);
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|!=
name|indexOptions
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
name|this
operator|.
name|indexOptions
operator|=
name|indexOptions
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// downgrade
name|this
operator|.
name|indexOptions
operator|=
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|indexOptions
argument_list|)
operator|<
literal|0
condition|?
name|this
operator|.
name|indexOptions
else|:
name|indexOptions
expr_stmt|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
condition|)
block|{
comment|// if updated field data is not for indexing, leave the updates out
name|this
operator|.
name|storeTermVector
operator||=
name|storeTermVector
expr_stmt|;
comment|// once vector, always vector
name|this
operator|.
name|storePayloads
operator||=
name|storePayloads
expr_stmt|;
comment|// Awkward: only drop norms if incoming update is indexed:
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
name|this
operator|.
name|omitNorms
operator|!=
name|omitNorms
condition|)
block|{
name|this
operator|.
name|omitNorms
operator|=
literal|true
expr_stmt|;
comment|// if one require omitNorms at least once, it remains off for life
block|}
block|}
if|if
condition|(
name|this
operator|.
name|indexOptions
operator|==
name|IndexOptions
operator|.
name|NONE
operator|||
name|this
operator|.
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// cannot store payloads if we don't store positions:
name|this
operator|.
name|storePayloads
operator|=
literal|false
expr_stmt|;
block|}
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
DECL|method|setDocValuesType
name|void
name|setDocValuesType
parameter_list|(
name|DocValuesType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"DocValuesType cannot be null (field: \""
operator|+
name|name
operator|+
literal|"\")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|docValuesType
operator|!=
name|DocValuesType
operator|.
name|NONE
operator|&&
name|docValuesType
operator|!=
name|type
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot change DocValues type from "
operator|+
name|docValuesType
operator|+
literal|" to "
operator|+
name|type
operator|+
literal|" for field \""
operator|+
name|name
operator|+
literal|"\""
argument_list|)
throw|;
block|}
name|docValuesType
operator|=
name|type
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/** Returns IndexOptions for the field, or IndexOptions.NONE if the field is not indexed */
DECL|method|getIndexOptions
specifier|public
name|IndexOptions
name|getIndexOptions
parameter_list|()
block|{
return|return
name|indexOptions
return|;
block|}
comment|/**    * Returns true if this field has any docValues.    */
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|docValuesType
operator|!=
name|DocValuesType
operator|.
name|NONE
return|;
block|}
comment|/**    * Returns {@link DocValuesType} of the docValues; this is    * {@code DocValuesType.NONE} if the field has no docvalues.    */
DECL|method|getDocValuesType
specifier|public
name|DocValuesType
name|getDocValuesType
parameter_list|()
block|{
return|return
name|docValuesType
return|;
block|}
comment|/** Sets the docValues generation of this field. */
DECL|method|setDocValuesGen
name|void
name|setDocValuesGen
parameter_list|(
name|long
name|dvGen
parameter_list|)
block|{
name|this
operator|.
name|dvGen
operator|=
name|dvGen
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/**    * Returns the docValues generation of this field, or -1 if no docValues    * updates exist for it.    */
DECL|method|getDocValuesGen
specifier|public
name|long
name|getDocValuesGen
parameter_list|()
block|{
return|return
name|dvGen
return|;
block|}
DECL|method|setStoreTermVectors
name|void
name|setStoreTermVectors
parameter_list|()
block|{
name|storeTermVector
operator|=
literal|true
expr_stmt|;
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
DECL|method|setStorePayloads
name|void
name|setStorePayloads
parameter_list|()
block|{
if|if
condition|(
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
operator|&&
name|indexOptions
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|storePayloads
operator|=
literal|true
expr_stmt|;
block|}
assert|assert
name|checkConsistency
argument_list|()
assert|;
block|}
comment|/**    * Returns true if norms are explicitly omitted for this field    */
DECL|method|omitsNorms
specifier|public
name|boolean
name|omitsNorms
parameter_list|()
block|{
return|return
name|omitNorms
return|;
block|}
comment|/**    * Returns true if this field actually has any norms.    */
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
return|return
name|isIndexed
argument_list|()
operator|&&
name|omitNorms
operator|==
literal|false
return|;
block|}
comment|/**    * Returns true if this field is indexed ({@link #getIndexOptions} is not IndexOptions.NONE).    */
DECL|method|isIndexed
specifier|public
name|boolean
name|isIndexed
parameter_list|()
block|{
return|return
name|indexOptions
operator|!=
name|IndexOptions
operator|.
name|NONE
return|;
block|}
comment|/**    * Returns true if any payloads exist for this field.    */
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|storePayloads
return|;
block|}
comment|/**    * Returns true if any term vectors exist for this field.    */
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
return|return
name|storeTermVector
return|;
block|}
comment|/**    * Get a codec attribute value, or null if it does not exist    */
DECL|method|getAttribute
specifier|public
name|String
name|getAttribute
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/**    * Puts a codec attribute value.    *<p>    * This is a key-value mapping for the field that the codec can use    * to store additional metadata, and will be available to the codec    * when reading the segment via {@link #getAttribute(String)}    *<p>    * If a value already exists for the field, it will be replaced with     * the new value.    */
DECL|method|putAttribute
specifier|public
name|String
name|putAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
return|return
name|attributes
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**    * Returns internal codec attributes map. May be null if no mappings exist.    */
DECL|method|attributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
block|}
end_class

end_unit

