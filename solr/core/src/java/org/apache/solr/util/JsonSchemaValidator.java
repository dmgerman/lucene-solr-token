begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|StrUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Utils
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
operator|.
name|identity
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toMap
import|;
end_import

begin_comment
comment|/**A very basic and lightweight json schema parsing and data validation tool. This custom tool is created  * because a) we need to support non json inputs b) to avoiding double parsing (this accepts an already parsed json as a map)  * It validates most aspects of json schema but it is NOT A FULLY COMPLIANT JSON schema parser or validator.  * What is supported ?  * a) all types and their validation (string, boolean, array, enum,object, integer, number)  * b) 'required' properties, 'additionalProperties'  *  *  */
end_comment

begin_class
DECL|class|JsonSchemaValidator
specifier|public
class|class
name|JsonSchemaValidator
block|{
DECL|field|root
specifier|private
specifier|final
name|SchemaNode
name|root
decl_stmt|;
DECL|method|JsonSchemaValidator
specifier|public
name|JsonSchemaValidator
parameter_list|(
name|String
name|jsonString
parameter_list|)
block|{
name|this
argument_list|(
operator|(
name|Map
operator|)
name|Utils
operator|.
name|fromJSONString
argument_list|(
name|jsonString
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|JsonSchemaValidator
specifier|public
name|JsonSchemaValidator
parameter_list|(
name|Map
name|jsonSchema
parameter_list|)
block|{
name|root
operator|=
operator|new
name|SchemaNode
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|root
operator|.
name|isRequired
operator|=
literal|true
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|errs
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|root
operator|.
name|validateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|errs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid schema. "
operator|+
name|StrUtils
operator|.
name|join
argument_list|(
name|errs
argument_list|,
literal|'|'
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|class|SchemaNode
specifier|private
specifier|static
class|class
name|SchemaNode
block|{
DECL|field|parent
specifier|final
name|SchemaNode
name|parent
decl_stmt|;
DECL|field|type
name|Type
name|type
decl_stmt|;
DECL|field|elementType
name|Type
name|elementType
decl_stmt|;
DECL|field|isRequired
name|boolean
name|isRequired
init|=
literal|false
decl_stmt|;
DECL|field|validationInfo
name|Object
name|validationInfo
decl_stmt|;
DECL|field|additionalProperties
name|Boolean
name|additionalProperties
decl_stmt|;
DECL|field|children
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaNode
argument_list|>
name|children
decl_stmt|;
DECL|method|SchemaNode
specifier|private
name|SchemaNode
parameter_list|(
name|SchemaNode
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
DECL|method|validateSchema
specifier|private
name|void
name|validateSchema
parameter_list|(
name|Map
name|jsonSchema
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
block|{
name|Object
name|typeStr
init|=
name|jsonSchema
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|typeStr
operator|==
literal|null
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"'type' is missing "
argument_list|)
expr_stmt|;
block|}
name|Type
name|type
init|=
name|Type
operator|.
name|get
argument_list|(
name|typeStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"Unknown type "
operator|+
name|typeStr
operator|+
literal|" in object "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|jsonSchema
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
for|for
control|(
name|SchemaAttribute
name|schemaAttribute
range|:
name|SchemaAttribute
operator|.
name|values
argument_list|()
control|)
block|{
name|schemaAttribute
operator|.
name|validateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|this
argument_list|,
name|errs
argument_list|)
expr_stmt|;
block|}
name|jsonSchema
operator|.
name|keySet
argument_list|()
operator|.
name|forEach
argument_list|(
name|o
lambda|->
block|{
if|if
condition|(
operator|!
name|knownAttributes
operator|.
name|containsKey
argument_list|(
name|o
argument_list|)
condition|)
name|errs
operator|.
name|add
argument_list|(
literal|"Unknown key : "
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|OBJECT
condition|)
block|{
name|Map
name|m
init|=
operator|(
name|Map
operator|)
name|jsonSchema
operator|.
name|get
argument_list|(
literal|"properties"
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
operator|.
name|Entry
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|od
init|=
operator|(
name|Map
operator|)
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
name|children
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|SchemaNode
name|child
init|=
operator|new
name|SchemaNode
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|children
operator|.
name|put
argument_list|(
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|child
operator|.
name|validateSchema
argument_list|(
name|od
argument_list|,
name|errs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"Invalid Object definition for field "
operator|+
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|additionalProperties
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
block|}
block|}
for|for
control|(
name|SchemaAttribute
name|attr
range|:
name|SchemaAttribute
operator|.
name|values
argument_list|()
control|)
block|{
name|attr
operator|.
name|postValidateSchema
argument_list|(
name|jsonSchema
argument_list|,
name|this
argument_list|,
name|errs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validate
specifier|private
name|void
name|validate
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|data
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
block|{
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|isRequired
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"Missing field '"
operator|+
name|key
operator|+
literal|"'"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|type
operator|.
name|validateData
argument_list|(
name|key
argument_list|,
name|data
argument_list|,
name|this
argument_list|,
name|errs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
if|if
condition|(
name|children
operator|!=
literal|null
operator|&&
name|type
operator|==
name|Type
operator|.
name|OBJECT
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SchemaNode
argument_list|>
name|e
range|:
name|children
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|validate
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
operator|(
name|Map
operator|)
name|data
operator|)
operator|.
name|get
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|errs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|!=
name|additionalProperties
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
operator|(
operator|(
name|Map
operator|)
name|data
operator|)
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|children
operator|.
name|containsKey
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"Unknown field '"
operator|+
name|o
operator|+
literal|"' in object : "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|validateJson
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|validateJson
parameter_list|(
name|Object
name|data
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|errs
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|root
operator|.
name|validate
argument_list|(
literal|null
argument_list|,
name|data
argument_list|,
name|errs
argument_list|)
expr_stmt|;
return|return
name|errs
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|errs
return|;
block|}
comment|/**represents an attribute in the schema definition    *    */
DECL|enum|SchemaAttribute
enum|enum
name|SchemaAttribute
block|{
DECL|enum constant|type
name|type
argument_list|(
literal|true
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
block|,
DECL|method|properties
DECL|method|properties
name|properties
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|OBJECT
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|validateSchema
parameter_list|(
name|Map
name|attrSchema
parameter_list|,
name|SchemaNode
name|schemaNode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
name|super
operator|.
name|validateSchema
argument_list|(
name|attrSchema
argument_list|,
name|schemaNode
argument_list|,
name|errors
argument_list|)
expr_stmt|;
if|if
condition|(
name|schemaNode
operator|.
name|type
operator|!=
name|Type
operator|.
name|OBJECT
condition|)
return|return;
name|Object
name|val
init|=
name|attrSchema
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|Object
name|additional
init|=
name|attrSchema
operator|.
name|get
argument_list|(
name|additionalProperties
operator|.
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|additional
argument_list|)
condition|)
name|schemaNode
operator|.
name|additionalProperties
operator|=
name|Boolean
operator|.
name|TRUE
expr_stmt|;
block|}
block|}
block|}
block|,
DECL|enum constant|additionalProperties
name|additionalProperties
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
block|,
DECL|method|items
DECL|method|items
name|items
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|OBJECT
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|validateSchema
parameter_list|(
name|Map
name|attrSchema
parameter_list|,
name|SchemaNode
name|schemaNode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
name|super
operator|.
name|validateSchema
argument_list|(
name|attrSchema
argument_list|,
name|schemaNode
argument_list|,
name|errors
argument_list|)
expr_stmt|;
name|Object
name|itemsVal
init|=
name|attrSchema
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|itemsVal
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|schemaNode
operator|.
name|type
operator|!=
name|Type
operator|.
name|ARRAY
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
literal|"Only 'array' can have 'items'"
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
if|if
condition|(
name|itemsVal
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|val
init|=
operator|(
name|Map
operator|)
name|itemsVal
decl_stmt|;
name|Object
name|value
init|=
name|val
operator|.
name|get
argument_list|(
name|type
operator|.
name|key
argument_list|)
decl_stmt|;
name|Type
name|t
init|=
name|Type
operator|.
name|get
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
literal|"Unknown array type "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|attrSchema
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|schemaNode
operator|.
name|elementType
operator|=
name|t
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|,
DECL|enum constant|__default
name|__default
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|UNKNOWN
argument_list|)
block|,
DECL|enum constant|description
name|description
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
block|,
DECL|enum constant|documentation
name|documentation
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
block|,
DECL|enum constant|oneOf
name|oneOf
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|)
block|,
DECL|method|__enum
DECL|method|__enum
name|__enum
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|)
block|{
annotation|@
name|Override
name|void
name|validateSchema
parameter_list|(
name|Map
name|attrSchema
parameter_list|,
name|SchemaNode
name|schemaNode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
if|if
condition|(
name|attrSchema
operator|.
name|get
argument_list|(
name|Type
operator|.
name|ENUM
operator|.
name|_name
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|schemaNode
operator|.
name|elementType
operator|=
name|schemaNode
operator|.
name|type
expr_stmt|;
name|schemaNode
operator|.
name|type
operator|=
name|Type
operator|.
name|ENUM
expr_stmt|;
block|}
block|}
annotation|@
name|Override
name|void
name|postValidateSchema
parameter_list|(
name|Map
name|attrSchema
parameter_list|,
name|SchemaNode
name|schemaNode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
block|{
name|Object
name|val
init|=
name|attrSchema
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|val
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|val
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|schemaNode
operator|.
name|elementType
operator|.
name|validate
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"Invalid value : "
operator|+
name|o
operator|+
literal|" Expected type : "
operator|+
name|schemaNode
operator|.
name|elementType
operator|.
name|_name
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|errs
operator|.
name|isEmpty
argument_list|()
condition|)
return|return;
name|schemaNode
operator|.
name|validationInfo
operator|=
operator|new
name|HashSet
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"'enum' should have a an array as value in Object "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|attrSchema
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|,
DECL|enum constant|id
name|id
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
block|,
DECL|enum constant|_ref
name|_ref
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
block|,
DECL|enum constant|_schema
name|_schema
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
block|,
DECL|method|required
DECL|method|required
name|required
argument_list|(
literal|false
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|postValidateSchema
parameter_list|(
name|Map
name|attrSchema
parameter_list|,
name|SchemaNode
name|attr
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
name|Object
name|val
init|=
name|attrSchema
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|List
condition|)
block|{
name|List
name|list
init|=
operator|(
name|List
operator|)
name|val
decl_stmt|;
if|if
condition|(
name|attr
operator|.
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|SchemaNode
argument_list|>
name|e
range|:
name|attr
operator|.
name|children
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|list
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|isRequired
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|;
DECL|field|key
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|_required
specifier|final
name|boolean
name|_required
decl_stmt|;
DECL|field|typ
specifier|final
name|Type
name|typ
decl_stmt|;
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|validateSchema
name|void
name|validateSchema
parameter_list|(
name|Map
name|attrSchema
parameter_list|,
name|SchemaNode
name|schemaNode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errors
parameter_list|)
block|{
name|Object
name|val
init|=
name|attrSchema
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|_required
condition|)
name|errors
operator|.
name|add
argument_list|(
literal|"Missing required attribute '"
operator|+
name|key
operator|+
literal|"' in object "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|attrSchema
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|typ
operator|.
name|validate
argument_list|(
name|val
argument_list|)
condition|)
name|errors
operator|.
name|add
argument_list|(
name|key
operator|+
literal|" should be of type "
operator|+
name|typ
operator|.
name|_name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|postValidateSchema
name|void
name|postValidateSchema
parameter_list|(
name|Map
name|attrSchema
parameter_list|,
name|SchemaNode
name|schemaNode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
block|{     }
DECL|method|SchemaAttribute
name|SchemaAttribute
parameter_list|(
name|boolean
name|required
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|name
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"__"
argument_list|,
literal|""
argument_list|)
operator|.
name|replace
argument_list|(
literal|'_'
argument_list|,
literal|'$'
argument_list|)
expr_stmt|;
name|this
operator|.
name|_required
operator|=
name|required
expr_stmt|;
name|this
operator|.
name|typ
operator|=
name|type
expr_stmt|;
block|}
block|}
DECL|interface|TypeValidator
interface|interface
name|TypeValidator
block|{
DECL|method|validateData
name|void
name|validateData
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|o
parameter_list|,
name|SchemaNode
name|schemaNode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
function_decl|;
block|}
comment|/**represents a type in json    *    */
DECL|enum|Type
enum|enum
name|Type
block|{
DECL|enum constant|STRING
name|STRING
argument_list|(
name|o
lambda|->
name|o
operator|instanceof
name|String
argument_list|)
block|,
DECL|enum constant|ARRAY
name|ARRAY
argument_list|(
name|o
lambda|->
name|o
operator|instanceof
name|List
argument_list|,
parameter_list|(
name|key
parameter_list|,
name|o
parameter_list|,
name|schemaNode
parameter_list|,
name|errs
parameter_list|)
lambda|->
block|{
name|List
name|l
operator|=
name|o
operator|instanceof
name|List
operator|?
operator|(
name|List
operator|)
name|o
operator|:
name|Collections
operator|.
name|singletonList
argument_list|(
name|o
argument_list|)
argument_list|;       if
operator|(
name|schemaNode
operator|.
name|elementType
operator|!=
literal|null
operator|)
block|{
for|for
control|(
name|Object
name|elem
range|:
name|l
control|)
block|{
if|if
condition|(
operator|!
name|schemaNode
operator|.
name|elementType
operator|.
name|validate
argument_list|(
name|elem
argument_list|)
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"Expected elements of type : "
operator|+
name|key
operator|+
literal|" but found : "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
end_class

begin_operator
unit|})
operator|,
end_operator

begin_expr_stmt
DECL|enum constant|NUMBER
name|NUMBER
argument_list|(
name|o
lambda|->
name|o
operator|instanceof
name|Number
argument_list|,
parameter_list|(
name|key
parameter_list|,
name|o
parameter_list|,
name|schemaNode
parameter_list|,
name|errs
parameter_list|)
lambda|->
block|{
lambda|if (o instanceof String
argument_list|)
block|{
try|try
block|{
name|Double
operator|.
name|parseDouble
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
end_expr_stmt

begin_catch
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|errs
operator|.
name|add
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_catch

begin_operator
unit|}      })
operator|,
end_operator

begin_expr_stmt
DECL|enum constant|INTEGER
name|INTEGER
argument_list|(
name|o
lambda|->
name|o
operator|instanceof
name|Integer
argument_list|,
parameter_list|(
name|key
parameter_list|,
name|o
parameter_list|,
name|schemaNode
parameter_list|,
name|errs
parameter_list|)
lambda|->
block|{
lambda|if (o instanceof String
argument_list|)
block|{
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
end_expr_stmt

begin_catch
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|errs
operator|.
name|add
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_catch

begin_operator
unit|}     })
operator|,
end_operator

begin_expr_stmt
DECL|enum constant|BOOLEAN
name|BOOLEAN
argument_list|(
name|o
lambda|->
name|o
operator|instanceof
name|Boolean
argument_list|,
parameter_list|(
name|key
parameter_list|,
name|o
parameter_list|,
name|schemaNode
parameter_list|,
name|errs
parameter_list|)
lambda|->
block|{
lambda|if (o instanceof String
argument_list|)
block|{
try|try
block|{
name|Boolean
operator|.
name|parseBoolean
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
end_expr_stmt

begin_catch
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|errs
operator|.
name|add
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_catch

begin_operator
unit|}     })
operator|,
end_operator

begin_expr_stmt
DECL|enum constant|ENUM
name|ENUM
argument_list|(
name|o
lambda|->
name|o
operator|instanceof
name|List
argument_list|,
parameter_list|(
name|key
parameter_list|,
name|o
parameter_list|,
name|schemaNode
parameter_list|,
name|errs
parameter_list|)
lambda|->
block|{
lambda|if (schemaNode.validationInfo instanceof HashSet
argument_list|)
block|{
name|HashSet
name|enumVals
operator|=
operator|(
name|HashSet
operator|)
name|schemaNode
operator|.
name|validationInfo
block|;
if|if
condition|(
operator|!
name|enumVals
operator|.
name|contains
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|errs
operator|.
name|add
argument_list|(
literal|"value of enum "
operator|+
name|key
operator|+
literal|" must be one of"
operator|+
name|enumVals
argument_list|)
expr_stmt|;
block|}
end_expr_stmt

begin_operator
unit|}     })
operator|,
end_operator

begin_expr_stmt
DECL|enum constant|OBJECT
name|OBJECT
argument_list|(
name|o
lambda|->
name|o
operator|instanceof
name|Map
argument_list|)
operator|,
DECL|enum constant|UNKNOWN
name|UNKNOWN
argument_list|(
operator|(
name|o
lambda|->
literal|true
operator|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
DECL|field|_name
specifier|final
name|String
name|_name
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|typeValidator
specifier|final
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
name|typeValidator
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|validator
specifier|private
specifier|final
name|TypeValidator
name|validator
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|Type
name|Type
argument_list|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
name|validator
argument_list|)
block|{
name|this
argument_list|(
name|validator
argument_list|,
literal|null
argument_list|)
block|;      }
DECL|method|Type
name|Type
argument_list|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
name|validator
argument_list|,
name|TypeValidator
name|v
argument_list|)
block|{
name|_name
operator|=
name|this
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
block|;
name|this
operator|.
name|typeValidator
operator|=
name|validator
block|;
name|this
operator|.
name|validator
operator|=
name|v
block|;     }
DECL|method|validate
name|boolean
name|validate
argument_list|(
name|Object
name|o
argument_list|)
block|{
return|return
name|typeValidator
operator|.
name|test
argument_list|(
name|o
argument_list|)
return|;
block|}
end_expr_stmt

begin_function
DECL|method|validateData
name|void
name|validateData
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|o
parameter_list|,
name|SchemaNode
name|attr
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|errs
parameter_list|)
block|{
if|if
condition|(
name|validator
operator|!=
literal|null
condition|)
block|{
name|validator
operator|.
name|validateData
argument_list|(
name|key
argument_list|,
name|o
argument_list|,
name|attr
argument_list|,
name|errs
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|typeValidator
operator|.
name|test
argument_list|(
name|o
argument_list|)
condition|)
name|errs
operator|.
name|add
argument_list|(
literal|"Expected type : "
operator|+
name|_name
operator|+
literal|" but found : "
operator|+
name|o
operator|+
literal|"in object : "
operator|+
name|Utils
operator|.
name|toJSONString
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|get
specifier|static
name|Type
name|get
parameter_list|(
name|Object
name|type
parameter_list|)
block|{
for|for
control|(
name|Type
name|t
range|:
name|Type
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|t
operator|.
name|_name
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
return|return
name|t
return|;
block|}
return|return
literal|null
return|;
block|}
end_function

begin_decl_stmt
unit|}     static
DECL|field|knownAttributes
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaAttribute
argument_list|>
name|knownAttributes
init|=
name|unmodifiableMap
argument_list|(
name|asList
argument_list|(
name|SchemaAttribute
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|toMap
argument_list|(
name|SchemaAttribute
operator|::
name|getKey
argument_list|,
name|identity
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
end_decl_stmt

unit|}
end_unit
