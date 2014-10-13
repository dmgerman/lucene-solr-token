begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

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
name|Collections
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
name|List
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
name|noggit
operator|.
name|JSONParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|noggit
operator|.
name|ObjectBuilder
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
name|emptyMap
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
name|singletonList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
operator|.
name|ZkNodeProps
operator|.
name|makeMap
import|;
end_import

begin_class
DECL|class|CommandOperation
specifier|public
class|class
name|CommandOperation
block|{
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|commandData
specifier|private
name|Object
name|commandData
decl_stmt|;
comment|//this is most often a map
DECL|field|errors
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|CommandOperation
name|CommandOperation
parameter_list|(
name|String
name|operationName
parameter_list|,
name|Object
name|metaData
parameter_list|)
block|{
name|commandData
operator|=
name|metaData
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|operationName
expr_stmt|;
block|}
DECL|method|getStr
specifier|public
name|String
name|getStr
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|def
parameter_list|)
block|{
if|if
condition|(
name|ROOT_OBJ
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|Object
name|obj
init|=
name|getRootPrimitive
argument_list|()
decl_stmt|;
return|return
name|obj
operator|==
name|def
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
return|;
block|}
name|String
name|s
init|=
operator|(
name|String
operator|)
name|getMapVal
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
name|def
else|:
name|s
return|;
block|}
DECL|method|getDataMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getDataMap
parameter_list|()
block|{
if|if
condition|(
name|commandData
operator|instanceof
name|Map
condition|)
block|{
return|return
operator|(
name|Map
operator|)
name|commandData
return|;
block|}
name|addError
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"The command {0} should have the values as a json object {key:val} format"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
block|}
DECL|method|getRootPrimitive
specifier|private
name|Object
name|getRootPrimitive
parameter_list|()
block|{
if|if
condition|(
name|commandData
operator|instanceof
name|Map
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
literal|"The value has to be a string for command : {1}"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|commandData
return|;
block|}
DECL|method|getVal
specifier|public
name|Object
name|getVal
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|getMapVal
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|getMapVal
specifier|private
name|Object
name|getMapVal
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|commandData
operator|instanceof
name|Map
condition|)
block|{
name|Map
name|metaData
init|=
operator|(
name|Map
operator|)
name|commandData
decl_stmt|;
return|return
name|metaData
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|msg
init|=
literal|" value has to be an object for operation :"
operator|+
name|name
decl_stmt|;
if|if
condition|(
operator|!
name|errors
operator|.
name|contains
argument_list|(
name|msg
argument_list|)
condition|)
name|errors
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getStrs
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStrs
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|val
init|=
name|getStrs
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|REQD
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|val
return|;
block|}
DECL|field|REQD
specifier|static
specifier|final
name|String
name|REQD
init|=
literal|"'{1}' is a required field"
decl_stmt|;
comment|/**Get collection of values for a key. If only one val is present a    * single value collection is returned    */
DECL|method|getStrs
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStrs
parameter_list|(
name|String
name|key
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|def
parameter_list|)
block|{
name|Object
name|v
init|=
name|getMapVal
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|def
return|;
block|}
else|else
block|{
if|if
condition|(
name|v
operator|instanceof
name|List
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
operator|(
name|List
operator|)
name|v
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|l
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|def
return|;
return|return
name|l
return|;
block|}
else|else
block|{
return|return
name|singletonList
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
comment|/**Get a required field. If missing it adds to the errors    */
DECL|method|getStr
specifier|public
name|String
name|getStr
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|ROOT_OBJ
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|Object
name|obj
init|=
name|getRootPrimitive
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|REQD
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|obj
operator|==
literal|null
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|obj
argument_list|)
return|;
block|}
name|String
name|s
init|=
name|getStr
argument_list|(
name|key
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
name|errors
operator|.
name|add
argument_list|(
name|MessageFormat
operator|.
name|format
argument_list|(
name|REQD
argument_list|,
name|key
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
DECL|method|errorDetails
specifier|private
name|Map
name|errorDetails
parameter_list|()
block|{
return|return
name|makeMap
argument_list|(
name|name
argument_list|,
name|commandData
argument_list|,
name|ERR_MSGS
argument_list|,
name|errors
argument_list|)
return|;
block|}
DECL|method|hasError
specifier|public
name|boolean
name|hasError
parameter_list|()
block|{
return|return
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|addError
specifier|public
name|void
name|addError
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|errors
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
return|return;
name|errors
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**Get all the values from the metadata for the command    * without the specified keys    */
DECL|method|getValuesExcluding
specifier|public
name|Map
name|getValuesExcluding
parameter_list|(
name|String
modifier|...
name|keys
parameter_list|)
block|{
name|getMapVal
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasError
argument_list|()
condition|)
return|return
name|emptyMap
argument_list|()
return|;
comment|//just to verify the type is Map
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cp
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
operator|)
name|commandData
argument_list|)
decl_stmt|;
if|if
condition|(
name|keys
operator|==
literal|null
condition|)
return|return
name|cp
return|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|cp
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|cp
return|;
block|}
DECL|method|getErrors
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getErrors
parameter_list|()
block|{
return|return
name|errors
return|;
block|}
DECL|field|ERR_MSGS
specifier|public
specifier|static
specifier|final
name|String
name|ERR_MSGS
init|=
literal|"errorMessages"
decl_stmt|;
DECL|field|ROOT_OBJ
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_OBJ
init|=
literal|""
decl_stmt|;
DECL|method|captureErrors
specifier|public
specifier|static
name|List
argument_list|<
name|Map
argument_list|>
name|captureErrors
parameter_list|(
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|ops
parameter_list|)
block|{
name|List
argument_list|<
name|Map
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CommandOperation
name|op
range|:
name|ops
control|)
block|{
if|if
condition|(
name|op
operator|.
name|hasError
argument_list|()
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
name|op
operator|.
name|errorDetails
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|errors
return|;
block|}
comment|/**Parse the command operations into command objects    */
DECL|method|parse
specifier|public
specifier|static
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|parse
parameter_list|(
name|Reader
name|rdr
parameter_list|)
throws|throws
name|IOException
block|{
name|JSONParser
name|parser
init|=
operator|new
name|JSONParser
argument_list|(
name|rdr
argument_list|)
decl_stmt|;
name|ObjectBuilder
name|ob
init|=
operator|new
name|ObjectBuilder
argument_list|(
name|parser
argument_list|)
decl_stmt|;
if|if
condition|(
name|parser
operator|.
name|lastEvent
argument_list|()
operator|!=
name|JSONParser
operator|.
name|OBJECT_START
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The JSON must be an Object of the form {\"command\": {...},..."
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|operations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|ev
init|=
name|parser
operator|.
name|nextEvent
argument_list|()
decl_stmt|;
if|if
condition|(
name|ev
operator|==
name|JSONParser
operator|.
name|OBJECT_END
condition|)
return|return
name|operations
return|;
name|Object
name|key
init|=
name|ob
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ev
operator|=
name|parser
operator|.
name|nextEvent
argument_list|()
expr_stmt|;
name|Object
name|val
init|=
name|ob
operator|.
name|getVal
argument_list|()
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
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|CommandOperation
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|CommandOperation
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|key
argument_list|)
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

