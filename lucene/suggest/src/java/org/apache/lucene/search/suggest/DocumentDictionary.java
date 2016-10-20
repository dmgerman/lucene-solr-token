begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package

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
name|document
operator|.
name|Document
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
name|index
operator|.
name|IndexableField
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
name|MultiDocValues
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
name|MultiFields
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
name|NumericDocValues
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
name|spell
operator|.
name|Dictionary
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
comment|/**  *<p>  * Dictionary with terms, weights, payload (optional) and contexts (optional)  * information taken from stored/indexed fields in a Lucene index.  *</p>  *<b>NOTE:</b>   *<ul>  *<li>  *      The term field has to be stored; if it is missing, the document is skipped.  *</li>  *<li>  *      The payload and contexts field are optional and are not required to be stored.  *</li>  *<li>  *      The weight field can be stored or can be a {@link NumericDocValues}.  *      If the weight field is not defined, the value of the weight is<code>0</code>  *</li>  *</ul>  */
end_comment

begin_class
DECL|class|DocumentDictionary
specifier|public
class|class
name|DocumentDictionary
implements|implements
name|Dictionary
block|{
comment|/** {@link IndexReader} to load documents from */
DECL|field|reader
specifier|protected
specifier|final
name|IndexReader
name|reader
decl_stmt|;
comment|/** Field to read payload from */
DECL|field|payloadField
specifier|protected
specifier|final
name|String
name|payloadField
decl_stmt|;
comment|/** Field to read contexts from */
DECL|field|contextsField
specifier|protected
specifier|final
name|String
name|contextsField
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|weightField
specifier|private
specifier|final
name|String
name|weightField
decl_stmt|;
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms and<code>weightField</code> for the weights that will be used for    * the corresponding terms.    */
DECL|method|DocumentDictionary
specifier|public
name|DocumentDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|weightField
parameter_list|)
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|weightField
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms,<code>weightField</code> for the weights that will be used for the     * the corresponding terms and<code>payloadField</code> for the corresponding payloads    * for the entry.    */
DECL|method|DocumentDictionary
specifier|public
name|DocumentDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|weightField
parameter_list|,
name|String
name|payloadField
parameter_list|)
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|field
argument_list|,
name|weightField
argument_list|,
name|payloadField
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new dictionary with the contents of the fields named<code>field</code>    * for the terms,<code>weightField</code> for the weights that will be used for the     * the corresponding terms,<code>payloadField</code> for the corresponding payloads    * for the entry and<code>contextsField</code> for associated contexts.    */
DECL|method|DocumentDictionary
specifier|public
name|DocumentDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|weightField
parameter_list|,
name|String
name|payloadField
parameter_list|,
name|String
name|contextsField
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|weightField
operator|=
name|weightField
expr_stmt|;
name|this
operator|.
name|payloadField
operator|=
name|payloadField
expr_stmt|;
name|this
operator|.
name|contextsField
operator|=
name|contextsField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEntryIterator
specifier|public
name|InputIterator
name|getEntryIterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DocumentInputIterator
argument_list|(
name|payloadField
operator|!=
literal|null
argument_list|,
name|contextsField
operator|!=
literal|null
argument_list|)
return|;
block|}
comment|/** Implements {@link InputIterator} from stored fields. */
DECL|class|DocumentInputIterator
specifier|protected
class|class
name|DocumentInputIterator
implements|implements
name|InputIterator
block|{
DECL|field|docCount
specifier|private
specifier|final
name|int
name|docCount
decl_stmt|;
DECL|field|relevantFields
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|relevantFields
decl_stmt|;
DECL|field|hasPayloads
specifier|private
specifier|final
name|boolean
name|hasPayloads
decl_stmt|;
DECL|field|hasContexts
specifier|private
specifier|final
name|boolean
name|hasContexts
decl_stmt|;
DECL|field|liveDocs
specifier|private
specifier|final
name|Bits
name|liveDocs
decl_stmt|;
DECL|field|currentDocId
specifier|private
name|int
name|currentDocId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentWeight
specifier|private
name|long
name|currentWeight
init|=
literal|0
decl_stmt|;
DECL|field|currentPayload
specifier|private
name|BytesRef
name|currentPayload
init|=
literal|null
decl_stmt|;
DECL|field|currentContexts
specifier|private
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|currentContexts
decl_stmt|;
DECL|field|weightValues
specifier|private
specifier|final
name|NumericDocValues
name|weightValues
decl_stmt|;
DECL|field|currentDocFields
name|IndexableField
index|[]
name|currentDocFields
init|=
operator|new
name|IndexableField
index|[
literal|0
index|]
decl_stmt|;
DECL|field|nextFieldsPosition
name|int
name|nextFieldsPosition
init|=
literal|0
decl_stmt|;
comment|/**      * Creates an iterator over term, weight and payload fields from the lucene      * index. setting<code>withPayload</code> to false, implies an iterator      * over only term and weight.      */
DECL|method|DocumentInputIterator
specifier|public
name|DocumentInputIterator
parameter_list|(
name|boolean
name|hasPayloads
parameter_list|,
name|boolean
name|hasContexts
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|hasPayloads
operator|=
name|hasPayloads
expr_stmt|;
name|this
operator|.
name|hasContexts
operator|=
name|hasContexts
expr_stmt|;
name|docCount
operator|=
name|reader
operator|.
name|maxDoc
argument_list|()
operator|-
literal|1
expr_stmt|;
name|weightValues
operator|=
operator|(
name|weightField
operator|!=
literal|null
operator|)
condition|?
name|MultiDocValues
operator|.
name|getNumericValues
argument_list|(
name|reader
argument_list|,
name|weightField
argument_list|)
else|:
literal|null
expr_stmt|;
name|liveDocs
operator|=
operator|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|MultiFields
operator|.
name|getLiveDocs
argument_list|(
name|reader
argument_list|)
else|:
literal|null
expr_stmt|;
name|relevantFields
operator|=
name|getRelevantFields
argument_list|(
operator|new
name|String
index|[]
block|{
name|field
block|,
name|weightField
block|,
name|payloadField
block|,
name|contextsField
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|currentWeight
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|nextFieldsPosition
operator|<
name|currentDocFields
operator|.
name|length
condition|)
block|{
comment|// Still values left from the document
name|IndexableField
name|fieldValue
init|=
name|currentDocFields
index|[
name|nextFieldsPosition
operator|++
index|]
decl_stmt|;
if|if
condition|(
name|fieldValue
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|fieldValue
operator|.
name|binaryValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|fieldValue
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|fieldValue
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
continue|continue;
block|}
block|}
if|if
condition|(
name|currentDocId
operator|==
name|docCount
condition|)
block|{
comment|// Iterated over all the documents.
break|break;
block|}
name|currentDocId
operator|++
expr_stmt|;
if|if
condition|(
name|liveDocs
operator|!=
literal|null
operator|&&
operator|!
name|liveDocs
operator|.
name|get
argument_list|(
name|currentDocId
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|currentDocId
argument_list|,
name|relevantFields
argument_list|)
decl_stmt|;
name|BytesRef
name|tempPayload
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|IndexableField
name|payload
init|=
name|doc
operator|.
name|getField
argument_list|(
name|payloadField
argument_list|)
decl_stmt|;
if|if
condition|(
name|payload
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|payload
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tempPayload
operator|=
name|payload
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|payload
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tempPayload
operator|=
operator|new
name|BytesRef
argument_list|(
name|payload
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// in case that the iterator has payloads configured, use empty values
comment|// instead of null for payload
if|if
condition|(
name|tempPayload
operator|==
literal|null
condition|)
block|{
name|tempPayload
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|tempContexts
decl_stmt|;
if|if
condition|(
name|hasContexts
condition|)
block|{
name|tempContexts
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
specifier|final
name|IndexableField
index|[]
name|contextFields
init|=
name|doc
operator|.
name|getFields
argument_list|(
name|contextsField
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexableField
name|contextField
range|:
name|contextFields
control|)
block|{
if|if
condition|(
name|contextField
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tempContexts
operator|.
name|add
argument_list|(
name|contextField
operator|.
name|binaryValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|contextField
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tempContexts
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|contextField
operator|.
name|stringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
continue|continue;
block|}
block|}
block|}
else|else
block|{
name|tempContexts
operator|=
name|Collections
operator|.
name|emptySet
argument_list|()
expr_stmt|;
block|}
name|currentDocFields
operator|=
name|doc
operator|.
name|getFields
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|nextFieldsPosition
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|currentDocFields
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// no values in this document
continue|continue;
block|}
name|IndexableField
name|fieldValue
init|=
name|currentDocFields
index|[
name|nextFieldsPosition
operator|++
index|]
decl_stmt|;
name|BytesRef
name|tempTerm
decl_stmt|;
if|if
condition|(
name|fieldValue
operator|.
name|binaryValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tempTerm
operator|=
name|fieldValue
operator|.
name|binaryValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldValue
operator|.
name|stringValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|tempTerm
operator|=
operator|new
name|BytesRef
argument_list|(
name|fieldValue
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
continue|continue;
block|}
name|currentPayload
operator|=
name|tempPayload
expr_stmt|;
name|currentContexts
operator|=
name|tempContexts
expr_stmt|;
name|currentWeight
operator|=
name|getWeight
argument_list|(
name|doc
argument_list|,
name|currentDocId
argument_list|)
expr_stmt|;
return|return
name|tempTerm
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
return|return
name|currentPayload
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|hasPayloads
return|;
block|}
comment|/**       * Returns the value of the<code>weightField</code> for the current document.      * Retrieves the value for the<code>weightField</code> if it's stored (using<code>doc</code>)      * or if it's indexed as {@link NumericDocValues} (using<code>docId</code>) for the document.      * If no value is found, then the weight is 0.      */
DECL|method|getWeight
specifier|protected
name|long
name|getWeight
parameter_list|(
name|Document
name|doc
parameter_list|,
name|int
name|docId
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexableField
name|weight
init|=
name|doc
operator|.
name|getField
argument_list|(
name|weightField
argument_list|)
decl_stmt|;
if|if
condition|(
name|weight
operator|!=
literal|null
condition|)
block|{
comment|// found weight as stored
return|return
operator|(
name|weight
operator|.
name|numericValue
argument_list|()
operator|!=
literal|null
operator|)
condition|?
name|weight
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
else|:
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|weightValues
operator|!=
literal|null
condition|)
block|{
comment|// found weight as NumericDocValue
if|if
condition|(
name|weightValues
operator|.
name|docID
argument_list|()
operator|<
name|docId
condition|)
block|{
name|weightValues
operator|.
name|advance
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|weightValues
operator|.
name|docID
argument_list|()
operator|==
name|docId
condition|)
block|{
return|return
name|weightValues
operator|.
name|longValue
argument_list|()
return|;
block|}
else|else
block|{
comment|// missing
return|return
literal|0
return|;
block|}
block|}
else|else
block|{
comment|// fall back
return|return
literal|0
return|;
block|}
block|}
DECL|method|getRelevantFields
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getRelevantFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|relevantFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|relevantField
range|:
name|fields
control|)
block|{
if|if
condition|(
name|relevantField
operator|!=
literal|null
condition|)
block|{
name|relevantFields
operator|.
name|add
argument_list|(
name|relevantField
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|relevantFields
return|;
block|}
annotation|@
name|Override
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
block|{
if|if
condition|(
name|hasContexts
condition|)
block|{
return|return
name|currentContexts
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
block|{
return|return
name|hasContexts
return|;
block|}
block|}
block|}
end_class

end_unit

