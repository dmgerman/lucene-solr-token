begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A concrete representation of a document within a Solr index.  Unlike a lucene  * Document, a SolrDocument may have an Object value matching the type defined in  * schema.xml  *   * For indexing documents, use the SolrInputDocumet that contains extra information  * for document and field boosting.  *   * @author ryan  * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|SolrDocument
specifier|public
class|class
name|SolrDocument
block|{
DECL|field|_fields
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
argument_list|>
name|_fields
init|=
literal|null
decl_stmt|;
DECL|method|SolrDocument
specifier|public
name|SolrDocument
parameter_list|()
block|{
name|_fields
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Let sub classes return something other then a List.      * Perhaps a Set or LinkedHashSet    */
DECL|method|getEmptyCollection
specifier|protected
name|Collection
argument_list|<
name|Object
argument_list|>
name|getEmptyCollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
return|;
block|}
comment|/**    * @return a list of fields defined in this document    */
DECL|method|getFieldNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFieldNames
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Add / Set / Remove Fields
comment|///////////////////////////////////////////////////////////////////
comment|/**    * Remove all fields with the name    */
DECL|method|removeFields
specifier|public
name|boolean
name|removeFields
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|remove
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Set a field with the given object.  If the object is an Array or Iterable, it will     * set multiple fields with the included contents.  This will replace any existing     * field with the given name    */
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|existing
init|=
name|_fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
name|existing
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * This will add a field to the document.  If fields already exist with this name    * it will append the collection    */
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|Collection
argument_list|<
name|Object
argument_list|>
name|existing
init|=
name|_fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|==
literal|null
condition|)
block|{
name|existing
operator|=
name|getEmptyCollection
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|_fields
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|existing
argument_list|)
expr_stmt|;
block|}
comment|// Arrays are iterable?  They appear to be, but not in the docs...
if|if
condition|(
name|value
operator|instanceof
name|Iterable
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
operator|(
name|Iterable
operator|)
name|value
control|)
block|{
name|this
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Object
index|[]
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
operator|(
name|Object
index|[]
operator|)
name|value
control|)
block|{
name|this
operator|.
name|addField
argument_list|(
name|name
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|existing
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|///////////////////////////////////////////////////////////////////
comment|// Get the field values
comment|///////////////////////////////////////////////////////////////////
comment|/**    * returns the first value for this field    */
DECL|method|getFieldValue
specifier|public
name|Object
name|getFieldValue
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Collection
name|v
init|=
name|_fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|v
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get a collection or all the values for a given field name    */
DECL|method|getFieldValues
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|getFieldValues
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|// TODO? should this be in the API?
comment|//  /**
comment|//   * Return a named list version
comment|//   */
comment|//  public NamedList<Object> toNamedList()
comment|//  {
comment|//    NamedList<Object> nl = new NamedList<Object>();
comment|//    for( Map.Entry<String, Collection<Object>> entry : _fields.entrySet() ) {
comment|//      Collection<Object> v = entry.getValue();
comment|//      if( v.size() == 0 ) {
comment|//        nl.add( entry.getKey(), null );
comment|//      }
comment|//      else if( v.size()> 1 ) {
comment|//        nl.add( entry.getKey(), v );
comment|//      }
comment|//      else { // Add a single value
comment|//        nl.add( entry.getKey(), v.iterator().next() );
comment|//      }
comment|//    }
comment|//    return nl;
comment|//  }
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SolrDocument["
operator|+
name|getFieldNames
argument_list|()
operator|+
literal|"]"
return|;
block|}
comment|/**    * Expose a Map interface to the solr field value collection.    */
DECL|method|getFieldValuesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|Object
argument_list|>
argument_list|>
name|getFieldValuesMap
parameter_list|()
block|{
return|return
name|_fields
return|;
block|}
comment|/**    * Expose a Map interface to the solr fields.  This function is useful for JSTL    */
DECL|method|getFieldValueMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFieldValueMap
parameter_list|()
block|{
return|return
operator|new
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
comment|/** Get the field Value */
specifier|public
name|Object
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|getFieldValue
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
return|;
block|}
comment|/** Set the field Value */
specifier|public
name|Object
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|setField
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/** Remove the field Value */
specifier|public
name|Object
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|removeFields
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Easily Supported methods
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|_fields
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|_fields
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|// Unsupported operations.  These are not necessary for JSTL
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Set
argument_list|<
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|String
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Collection
argument_list|<
name|Object
argument_list|>
name|values
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

