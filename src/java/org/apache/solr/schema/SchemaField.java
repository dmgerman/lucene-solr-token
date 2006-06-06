begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

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
name|Field
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
name|SortField
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
name|request
operator|.
name|XMLWriter
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Encapsulates all information about a Field in a Solr Schema  *  * @author yonik  * @version $Id$  */
end_comment

begin_class
DECL|class|SchemaField
specifier|public
specifier|final
class|class
name|SchemaField
extends|extends
name|FieldProperties
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|final
name|FieldType
name|type
decl_stmt|;
DECL|field|properties
specifier|final
name|int
name|properties
decl_stmt|;
comment|/** Create a new SchemaField with the given name and type,    *  using all the default properties from the type.    */
DECL|method|SchemaField
specifier|public
name|SchemaField
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|type
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|type
operator|.
name|properties
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new SchemaField from an existing one by using all    * of the properties of the prototype except the field name.    */
DECL|method|SchemaField
specifier|public
name|SchemaField
parameter_list|(
name|SchemaField
name|prototype
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|prototype
operator|.
name|type
argument_list|,
name|prototype
operator|.
name|properties
argument_list|)
expr_stmt|;
block|}
comment|/** Create a new SchemaField with the given name and type,    * and with the specified properties.  Properties are *not*    * inherited from the type in this case, so users of this    * constructor should derive the properties from type.getProperties()    *  using all the default properties from the type.    */
DECL|method|SchemaField
specifier|public
name|SchemaField
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|type
parameter_list|,
name|int
name|properties
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getType
specifier|public
name|FieldType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getProperties
name|int
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
DECL|method|indexed
specifier|public
name|boolean
name|indexed
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|INDEXED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|stored
specifier|public
name|boolean
name|stored
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|storeTermVector
specifier|public
name|boolean
name|storeTermVector
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORE_TERMVECTORS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|storeTermPositions
specifier|public
name|boolean
name|storeTermPositions
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORE_TERMPOSITIONS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|storeTermOffsets
specifier|public
name|boolean
name|storeTermOffsets
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|STORE_TERMOFFSETS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|omitNorms
specifier|public
name|boolean
name|omitNorms
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|OMIT_NORMS
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|multiValued
specifier|public
name|boolean
name|multiValued
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|MULTIVALUED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|sortMissingFirst
specifier|public
name|boolean
name|sortMissingFirst
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|SORT_MISSING_FIRST
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|sortMissingLast
specifier|public
name|boolean
name|sortMissingLast
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|SORT_MISSING_LAST
operator|)
operator|!=
literal|0
return|;
block|}
comment|// things that should be determined by field type, not set as options
DECL|method|isTokenized
name|boolean
name|isTokenized
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|TOKENIZED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|isBinary
name|boolean
name|isBinary
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|BINARY
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|isCompressed
name|boolean
name|isCompressed
parameter_list|()
block|{
return|return
operator|(
name|properties
operator|&
name|COMPRESSED
operator|)
operator|!=
literal|0
return|;
block|}
DECL|method|createField
specifier|public
name|Field
name|createField
parameter_list|(
name|String
name|val
parameter_list|,
name|float
name|boost
parameter_list|)
block|{
return|return
name|type
operator|.
name|createField
argument_list|(
name|this
argument_list|,
name|val
argument_list|,
name|boost
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
operator|+
literal|"{type="
operator|+
name|type
operator|.
name|getTypeName
argument_list|()
operator|+
literal|",properties="
operator|+
name|propertiesToString
argument_list|(
name|properties
argument_list|)
operator|+
literal|"}"
return|;
block|}
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|XMLWriter
name|writer
parameter_list|,
name|String
name|name
parameter_list|,
name|Field
name|val
parameter_list|)
throws|throws
name|IOException
block|{
comment|// name is passed in because it may be null if name should not be used.
name|type
operator|.
name|write
argument_list|(
name|writer
argument_list|,
name|name
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|getSortField
specifier|public
name|SortField
name|getSortField
parameter_list|(
name|boolean
name|top
parameter_list|)
block|{
return|return
name|type
operator|.
name|getSortField
argument_list|(
name|this
argument_list|,
name|top
argument_list|)
return|;
block|}
DECL|method|create
specifier|static
name|SchemaField
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|FieldType
name|ft
parameter_list|,
name|Map
name|props
parameter_list|)
block|{
name|int
name|trueProps
init|=
name|parseProperties
argument_list|(
name|props
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|int
name|falseProps
init|=
name|parseProperties
argument_list|(
name|props
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|int
name|p
init|=
name|ft
operator|.
name|properties
decl_stmt|;
comment|//
comment|// If any properties were explicitly turned off, then turn off other properties
comment|// that depend on that.
comment|//
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|STORED
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
name|STORED
operator||
name|BINARY
operator||
name|COMPRESSED
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|trueProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting stored field options:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|INDEXED
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
operator|(
name|INDEXED
operator||
name|OMIT_NORMS
operator||
name|STORE_TERMVECTORS
operator||
name|STORE_TERMPOSITIONS
operator||
name|STORE_TERMOFFSETS
operator||
name|SORT_MISSING_FIRST
operator||
name|SORT_MISSING_LAST
operator|)
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|trueProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting indexed field options:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|falseProps
argument_list|,
name|STORE_TERMVECTORS
argument_list|)
condition|)
block|{
name|int
name|pp
init|=
operator|(
name|STORE_TERMVECTORS
operator||
name|STORE_TERMPOSITIONS
operator||
name|STORE_TERMOFFSETS
operator|)
decl_stmt|;
if|if
condition|(
name|on
argument_list|(
name|pp
argument_list|,
name|trueProps
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SchemaField: "
operator|+
name|name
operator|+
literal|" conflicting termvector field options:"
operator|+
name|props
argument_list|)
throw|;
block|}
name|p
operator|&=
operator|~
name|pp
expr_stmt|;
block|}
comment|// override sort flags
if|if
condition|(
name|on
argument_list|(
name|trueProps
argument_list|,
name|SORT_MISSING_FIRST
argument_list|)
condition|)
block|{
name|p
operator|&=
operator|~
name|SORT_MISSING_LAST
expr_stmt|;
block|}
if|if
condition|(
name|on
argument_list|(
name|trueProps
argument_list|,
name|SORT_MISSING_LAST
argument_list|)
condition|)
block|{
name|p
operator|&=
operator|~
name|SORT_MISSING_FIRST
expr_stmt|;
block|}
name|p
operator|&=
operator|~
name|falseProps
expr_stmt|;
name|p
operator||=
name|trueProps
expr_stmt|;
return|return
operator|new
name|SchemaField
argument_list|(
name|name
argument_list|,
name|ft
argument_list|,
name|p
argument_list|)
return|;
block|}
block|}
end_class

end_unit

