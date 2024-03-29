begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.response
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
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
name|List
import|;
end_import

begin_comment
comment|/**  * This class represents the result of a group command.  * This can be the result of the following parameter:  *<ul>  *<li> group.field  *<li> group.func  *<li> group.query  *</ul>  *  * An instance of this class contains:  *<ul>  *<li> The name of this command. This can be the field, function or query grouped by.  *<li> The total number of documents that have matched.  *<li> The total number of groups that have matched.  *<li> The groups to be displayed. Depending on the start and rows parameter.  *</ul>  *  * In case of<code>group.query</code> only one group is present and ngroups is always<code>null</code>.  *  * @since solr 3.4  */
end_comment

begin_class
DECL|class|GroupCommand
specifier|public
class|class
name|GroupCommand
implements|implements
name|Serializable
block|{
DECL|field|_name
specifier|private
specifier|final
name|String
name|_name
decl_stmt|;
DECL|field|_values
specifier|private
specifier|final
name|List
argument_list|<
name|Group
argument_list|>
name|_values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|_matches
specifier|private
specifier|final
name|int
name|_matches
decl_stmt|;
DECL|field|_ngroups
specifier|private
specifier|final
name|Integer
name|_ngroups
decl_stmt|;
comment|/**    * Creates a GroupCommand instance    *    * @param name    The name of this command    * @param matches The total number of documents found for this command    */
DECL|method|GroupCommand
specifier|public
name|GroupCommand
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|matches
parameter_list|)
block|{
name|_name
operator|=
name|name
expr_stmt|;
name|_matches
operator|=
name|matches
expr_stmt|;
name|_ngroups
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Creates a GroupCommand instance.    *    * @param name    The name of this command    * @param matches The total number of documents found for this command    * @param nGroups The total number of groups found for this command.    */
DECL|method|GroupCommand
specifier|public
name|GroupCommand
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|matches
parameter_list|,
name|int
name|nGroups
parameter_list|)
block|{
name|_name
operator|=
name|name
expr_stmt|;
name|_matches
operator|=
name|matches
expr_stmt|;
name|_ngroups
operator|=
name|nGroups
expr_stmt|;
block|}
comment|/**    * Returns the name of this command. This can be the field, function or query grouped by.    *    * @return the name of this command    */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|_name
return|;
block|}
comment|/**    * Adds a group to this command.    *    * @param group A group to be added    */
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Group
name|group
parameter_list|)
block|{
name|_values
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the groups to be displayed.    * The number of groups returned depend on the<code>start</code> and<code>rows</code> parameters.    *    * @return the groups to be displayed.    */
DECL|method|getValues
specifier|public
name|List
argument_list|<
name|Group
argument_list|>
name|getValues
parameter_list|()
block|{
return|return
name|_values
return|;
block|}
comment|/**    * Returns the total number of documents found for this command.    *    * @return the total number of documents found for this command.    */
DECL|method|getMatches
specifier|public
name|int
name|getMatches
parameter_list|()
block|{
return|return
name|_matches
return|;
block|}
comment|/**    * Returns the total number of groups found for this command.    * Returns<code>null</code> if the<code>group.ngroups</code> parameter is unset or<code>false</code> or    * if this is a group command query (parameter =<code>group.query</code>).    *    * @return the total number of groups found for this command.    */
DECL|method|getNGroups
specifier|public
name|Integer
name|getNGroups
parameter_list|()
block|{
return|return
name|_ngroups
return|;
block|}
block|}
end_class

end_unit

