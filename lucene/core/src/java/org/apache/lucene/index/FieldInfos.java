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
name|SortedMap
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
operator|.
name|IndexOptions
import|;
end_import

begin_comment
comment|/**   * Collection of {@link FieldInfo}s (accessible by number or by name).  *  @lucene.experimental  */
end_comment

begin_class
DECL|class|FieldInfos
specifier|public
class|class
name|FieldInfos
implements|implements
name|Iterable
argument_list|<
name|FieldInfo
argument_list|>
block|{
DECL|field|hasFreq
specifier|private
specifier|final
name|boolean
name|hasFreq
decl_stmt|;
DECL|field|hasProx
specifier|private
specifier|final
name|boolean
name|hasProx
decl_stmt|;
DECL|field|hasVectors
specifier|private
specifier|final
name|boolean
name|hasVectors
decl_stmt|;
DECL|field|hasNorms
specifier|private
specifier|final
name|boolean
name|hasNorms
decl_stmt|;
DECL|field|hasDocValues
specifier|private
specifier|final
name|boolean
name|hasDocValues
decl_stmt|;
DECL|field|byNumber
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
name|byNumber
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|byName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|Collection
argument_list|<
name|FieldInfo
argument_list|>
name|values
decl_stmt|;
comment|// for an unmodifiable iterator
DECL|method|FieldInfos
specifier|public
name|FieldInfos
parameter_list|(
name|FieldInfo
index|[]
name|infos
parameter_list|)
block|{
name|boolean
name|hasVectors
init|=
literal|false
decl_stmt|;
name|boolean
name|hasProx
init|=
literal|false
decl_stmt|;
name|boolean
name|hasFreq
init|=
literal|false
decl_stmt|;
name|boolean
name|hasNorms
init|=
literal|false
decl_stmt|;
name|boolean
name|hasDocValues
init|=
literal|false
decl_stmt|;
for|for
control|(
name|FieldInfo
name|info
range|:
name|infos
control|)
block|{
assert|assert
operator|!
name|byNumber
operator|.
name|containsKey
argument_list|(
name|info
operator|.
name|number
argument_list|)
assert|;
name|byNumber
operator|.
name|put
argument_list|(
name|info
operator|.
name|number
argument_list|,
name|info
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|byName
operator|.
name|containsKey
argument_list|(
name|info
operator|.
name|name
argument_list|)
assert|;
name|byName
operator|.
name|put
argument_list|(
name|info
operator|.
name|name
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|hasVectors
operator||=
name|info
operator|.
name|hasVectors
argument_list|()
expr_stmt|;
name|hasProx
operator||=
name|info
operator|.
name|isIndexed
argument_list|()
operator|&&
name|info
operator|.
name|getIndexOptions
argument_list|()
operator|.
name|compareTo
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
operator|>=
literal|0
expr_stmt|;
name|hasFreq
operator||=
name|info
operator|.
name|isIndexed
argument_list|()
operator|&&
name|info
operator|.
name|getIndexOptions
argument_list|()
operator|!=
name|IndexOptions
operator|.
name|DOCS_ONLY
expr_stmt|;
name|hasNorms
operator||=
name|info
operator|.
name|hasNorms
argument_list|()
expr_stmt|;
name|hasDocValues
operator||=
name|info
operator|.
name|hasDocValues
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|hasVectors
operator|=
name|hasVectors
expr_stmt|;
name|this
operator|.
name|hasProx
operator|=
name|hasProx
expr_stmt|;
name|this
operator|.
name|hasFreq
operator|=
name|hasFreq
expr_stmt|;
name|this
operator|.
name|hasNorms
operator|=
name|hasNorms
expr_stmt|;
name|this
operator|.
name|hasDocValues
operator|=
name|hasDocValues
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|byNumber
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Returns true if any fields have freqs */
DECL|method|hasFreq
specifier|public
name|boolean
name|hasFreq
parameter_list|()
block|{
return|return
name|hasFreq
return|;
block|}
comment|/** Returns true if any fields have positions */
DECL|method|hasProx
specifier|public
name|boolean
name|hasProx
parameter_list|()
block|{
return|return
name|hasProx
return|;
block|}
comment|/**    * @return true if at least one field has any vectors    */
DECL|method|hasVectors
specifier|public
name|boolean
name|hasVectors
parameter_list|()
block|{
return|return
name|hasVectors
return|;
block|}
comment|/**    * @return true if at least one field has any norms    */
DECL|method|hasNorms
specifier|public
name|boolean
name|hasNorms
parameter_list|()
block|{
return|return
name|hasNorms
return|;
block|}
comment|/**    * @return true if at least one field has doc values    */
DECL|method|hasDocValues
specifier|public
name|boolean
name|hasDocValues
parameter_list|()
block|{
return|return
name|hasDocValues
return|;
block|}
comment|/**    * @return number of fields    */
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
assert|assert
name|byNumber
operator|.
name|size
argument_list|()
operator|==
name|byName
operator|.
name|size
argument_list|()
assert|;
return|return
name|byNumber
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Returns an iterator over all the fieldinfo objects present,    * ordered by ascending field number    */
comment|// TODO: what happens if in fact a different order is used?
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|FieldInfo
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|values
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**    * Return the fieldinfo object referenced by the field name    * @return the FieldInfo object or null when the given fieldName    * doesn't exist.    */
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
comment|/**    * Return the fieldinfo object referenced by the fieldNumber.    * @param fieldNumber    * @return the FieldInfo object or null when the given fieldNumber    * doesn't exist.    */
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|int
name|fieldNumber
parameter_list|)
block|{
return|return
operator|(
name|fieldNumber
operator|>=
literal|0
operator|)
condition|?
name|byNumber
operator|.
name|get
argument_list|(
name|fieldNumber
argument_list|)
else|:
literal|null
return|;
block|}
DECL|class|FieldNumberBiMap
specifier|static
specifier|final
class|class
name|FieldNumberBiMap
block|{
DECL|field|numberToName
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|numberToName
decl_stmt|;
DECL|field|nameToNumber
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nameToNumber
decl_stmt|;
DECL|field|lowestUnassignedFieldNumber
specifier|private
name|int
name|lowestUnassignedFieldNumber
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|FieldNumberBiMap
name|FieldNumberBiMap
parameter_list|()
block|{
name|this
operator|.
name|nameToNumber
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|numberToName
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the global field number for the given field name. If the name      * does not exist yet it tries to add it with the given preferred field      * number assigned if possible otherwise the first unassigned field number      * is used as the field number.      */
DECL|method|addOrGet
specifier|synchronized
name|int
name|addOrGet
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|int
name|preferredFieldNumber
parameter_list|)
block|{
name|Integer
name|fieldNumber
init|=
name|nameToNumber
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldNumber
operator|==
literal|null
condition|)
block|{
specifier|final
name|Integer
name|preferredBoxed
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|preferredFieldNumber
argument_list|)
decl_stmt|;
if|if
condition|(
name|preferredFieldNumber
operator|!=
operator|-
literal|1
operator|&&
operator|!
name|numberToName
operator|.
name|containsKey
argument_list|(
name|preferredBoxed
argument_list|)
condition|)
block|{
comment|// cool - we can use this number globally
name|fieldNumber
operator|=
name|preferredBoxed
expr_stmt|;
block|}
else|else
block|{
comment|// find a new FieldNumber
while|while
condition|(
name|numberToName
operator|.
name|containsKey
argument_list|(
operator|++
name|lowestUnassignedFieldNumber
argument_list|)
condition|)
block|{
comment|// might not be up to date - lets do the work once needed
block|}
name|fieldNumber
operator|=
name|lowestUnassignedFieldNumber
expr_stmt|;
block|}
name|numberToName
operator|.
name|put
argument_list|(
name|fieldNumber
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|nameToNumber
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|fieldNumber
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldNumber
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/**      * Sets the given field number and name if not yet set.       */
DECL|method|setIfNotSet
specifier|synchronized
name|void
name|setIfNotSet
parameter_list|(
name|int
name|fieldNumber
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
specifier|final
name|Integer
name|boxedFieldNumber
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|fieldNumber
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|numberToName
operator|.
name|containsKey
argument_list|(
name|boxedFieldNumber
argument_list|)
operator|&&
operator|!
name|nameToNumber
operator|.
name|containsKey
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|numberToName
operator|.
name|put
argument_list|(
name|boxedFieldNumber
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
name|nameToNumber
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|boxedFieldNumber
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|containsConsistent
argument_list|(
name|boxedFieldNumber
argument_list|,
name|fieldName
argument_list|)
assert|;
block|}
block|}
comment|// used by assert
DECL|method|containsConsistent
specifier|synchronized
name|boolean
name|containsConsistent
parameter_list|(
name|Integer
name|number
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
name|numberToName
operator|.
name|get
argument_list|(
name|number
argument_list|)
argument_list|)
operator|&&
name|number
operator|.
name|equals
argument_list|(
name|nameToNumber
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|Builder
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|byName
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
name|byName
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|FieldInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|globalFieldNumbers
specifier|final
name|FieldNumberBiMap
name|globalFieldNumbers
decl_stmt|;
DECL|method|Builder
name|Builder
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|FieldNumberBiMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new instance with the given {@link FieldNumberBiMap}.       */
DECL|method|Builder
name|Builder
parameter_list|(
name|FieldNumberBiMap
name|globalFieldNumbers
parameter_list|)
block|{
assert|assert
name|globalFieldNumbers
operator|!=
literal|null
assert|;
name|this
operator|.
name|globalFieldNumbers
operator|=
name|globalFieldNumbers
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|FieldInfos
name|other
parameter_list|)
block|{
for|for
control|(
name|FieldInfo
name|fieldInfo
range|:
name|other
control|)
block|{
name|add
argument_list|(
name|fieldInfo
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * adds the given field to this FieldInfos name / number mapping. The given FI      * must be present in the global field number mapping before this method it      * called      */
DECL|method|putInternal
specifier|private
name|void
name|putInternal
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
assert|assert
operator|!
name|byName
operator|.
name|containsKey
argument_list|(
name|fi
operator|.
name|name
argument_list|)
assert|;
assert|assert
name|globalFieldNumbers
operator|.
name|containsConsistent
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|fi
operator|.
name|number
argument_list|)
argument_list|,
name|fi
operator|.
name|name
argument_list|)
assert|;
name|byName
operator|.
name|put
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
argument_list|)
expr_stmt|;
block|}
comment|/** If the field is not yet known, adds it. If it is known, checks to make      *  sure that the isIndexed flag is the same as was given previously for this      *  field. If not - marks it as being indexed.  Same goes for the TermVector      * parameters.      *      * @param name The name of the field      * @param isIndexed true if the field is indexed      * @param storeTermVector true if the term vector should be stored      * @param omitNorms true if the norms for the indexed field should be omitted      * @param storePayloads true if payloads should be stored for this field      * @param indexOptions if term freqs should be omitted for this field      */
comment|// nocommit: fix testCodecs to do this another way, its the only user of this
DECL|method|addOrUpdate
name|FieldInfo
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isIndexed
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
name|DocValues
operator|.
name|Type
name|docValues
parameter_list|,
name|DocValues
operator|.
name|Type
name|normType
parameter_list|)
block|{
return|return
name|addOrUpdateInternal
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValues
argument_list|,
name|normType
argument_list|)
return|;
block|}
comment|// NOTE: this method does not carry over termVector
comment|// booleans nor docValuesType; the indexer chain
comment|// (TermVectorsConsumerPerField, DocFieldProcessor) must
comment|// set these fields when they succeed in consuming
comment|// the document:
DECL|method|addOrUpdate
specifier|public
name|FieldInfo
name|addOrUpdate
parameter_list|(
name|String
name|name
parameter_list|,
name|IndexableFieldType
name|fieldType
parameter_list|)
block|{
comment|// TODO: really, indexer shouldn't even call this
comment|// method (it's only called from DocFieldProcessor);
comment|// rather, each component in the chain should update
comment|// what it "owns".  EG fieldType.indexOptions() should
comment|// be updated by maybe FreqProxTermsWriterPerField:
return|return
name|addOrUpdateInternal
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
name|fieldType
operator|.
name|indexed
argument_list|()
argument_list|,
literal|false
argument_list|,
name|fieldType
operator|.
name|omitNorms
argument_list|()
argument_list|,
literal|false
argument_list|,
name|fieldType
operator|.
name|indexOptions
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|addOrUpdateInternal
specifier|private
name|FieldInfo
name|addOrUpdateInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|preferredFieldNumber
parameter_list|,
name|boolean
name|isIndexed
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
name|DocValues
operator|.
name|Type
name|docValues
parameter_list|,
name|DocValues
operator|.
name|Type
name|normType
parameter_list|)
block|{
name|FieldInfo
name|fi
init|=
name|fieldInfo
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|==
literal|null
condition|)
block|{
comment|// get a global number for this field
specifier|final
name|int
name|fieldNumber
init|=
name|globalFieldNumbers
operator|.
name|addOrGet
argument_list|(
name|name
argument_list|,
name|preferredFieldNumber
argument_list|)
decl_stmt|;
name|fi
operator|=
name|addInternal
argument_list|(
name|name
argument_list|,
name|fieldNumber
argument_list|,
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValues
argument_list|,
name|normType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fi
operator|.
name|update
argument_list|(
name|isIndexed
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|docValues
operator|!=
literal|null
condition|)
block|{
name|fi
operator|.
name|setDocValuesType
argument_list|(
name|docValues
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|normType
operator|!=
literal|null
condition|)
block|{
name|fi
operator|.
name|setNormValueType
argument_list|(
name|normType
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fi
return|;
block|}
DECL|method|add
specifier|public
name|FieldInfo
name|add
parameter_list|(
name|FieldInfo
name|fi
parameter_list|)
block|{
comment|// IMPORTANT - reuse the field number if possible for consistent field numbers across segments
return|return
name|addOrUpdateInternal
argument_list|(
name|fi
operator|.
name|name
argument_list|,
name|fi
operator|.
name|number
argument_list|,
name|fi
operator|.
name|isIndexed
argument_list|()
argument_list|,
name|fi
operator|.
name|hasVectors
argument_list|()
argument_list|,
name|fi
operator|.
name|omitsNorms
argument_list|()
argument_list|,
name|fi
operator|.
name|hasPayloads
argument_list|()
argument_list|,
name|fi
operator|.
name|getIndexOptions
argument_list|()
argument_list|,
name|fi
operator|.
name|getDocValuesType
argument_list|()
argument_list|,
name|fi
operator|.
name|getNormType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|addInternal
specifier|private
name|FieldInfo
name|addInternal
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|fieldNumber
parameter_list|,
name|boolean
name|isIndexed
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
name|DocValues
operator|.
name|Type
name|docValuesType
parameter_list|,
name|DocValues
operator|.
name|Type
name|normType
parameter_list|)
block|{
name|globalFieldNumbers
operator|.
name|setIfNotSet
argument_list|(
name|fieldNumber
argument_list|,
name|name
argument_list|)
expr_stmt|;
specifier|final
name|FieldInfo
name|fi
init|=
operator|new
name|FieldInfo
argument_list|(
name|name
argument_list|,
name|isIndexed
argument_list|,
name|fieldNumber
argument_list|,
name|storeTermVector
argument_list|,
name|omitNorms
argument_list|,
name|storePayloads
argument_list|,
name|indexOptions
argument_list|,
name|docValuesType
argument_list|,
name|normType
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|putInternal
argument_list|(
name|fi
argument_list|)
expr_stmt|;
return|return
name|fi
return|;
block|}
DECL|method|fieldInfo
specifier|public
name|FieldInfo
name|fieldInfo
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|byName
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
return|;
block|}
DECL|method|finish
specifier|final
name|FieldInfos
name|finish
parameter_list|()
block|{
return|return
operator|new
name|FieldInfos
argument_list|(
name|byName
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|FieldInfo
index|[
name|byName
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

