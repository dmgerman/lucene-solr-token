begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.config
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|config
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|standard
operator|.
name|processors
operator|.
name|GroupQueryNodeProcessor
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
name|AttributeImpl
import|;
end_import

begin_comment
comment|/**  * This attribute is used by {@link GroupQueryNodeProcessor} processor and must  * be defined in the {@link QueryConfigHandler}. This attribute tells the  * processor which is the default boolean operator when no operator is defined  * between terms.<br/>  *   * @see org.apache.lucene.queryParser.standard.config.DefaultOperatorAttribute  */
end_comment

begin_class
DECL|class|DefaultOperatorAttributeImpl
specifier|public
class|class
name|DefaultOperatorAttributeImpl
extends|extends
name|AttributeImpl
implements|implements
name|DefaultOperatorAttribute
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6804760312723049526L
decl_stmt|;
DECL|field|operator
specifier|private
name|Operator
name|operator
init|=
name|Operator
operator|.
name|OR
decl_stmt|;
DECL|method|DefaultOperatorAttributeImpl
specifier|public
name|DefaultOperatorAttributeImpl
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|setOperator
specifier|public
name|void
name|setOperator
parameter_list|(
name|Operator
name|operator
parameter_list|)
block|{
if|if
condition|(
name|operator
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"default operator cannot be null!"
argument_list|)
throw|;
block|}
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
block|}
DECL|method|getOperator
specifier|public
name|Operator
name|getOperator
parameter_list|()
block|{
return|return
name|this
operator|.
name|operator
return|;
block|}
annotation|@
name|Override
DECL|method|clear
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
annotation|@
name|Override
DECL|method|copyTo
specifier|public
name|void
name|copyTo
parameter_list|(
name|AttributeImpl
name|target
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|DefaultOperatorAttributeImpl
condition|)
block|{
name|DefaultOperatorAttributeImpl
name|defaultOperatorAttr
init|=
operator|(
name|DefaultOperatorAttributeImpl
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|defaultOperatorAttr
operator|.
name|getOperator
argument_list|()
operator|==
name|this
operator|.
name|getOperator
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getOperator
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<defaultOperatorAttribute operator="
operator|+
name|this
operator|.
name|operator
operator|.
name|name
argument_list|()
operator|+
literal|"/>"
return|;
block|}
block|}
end_class

end_unit

