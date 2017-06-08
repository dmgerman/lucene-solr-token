begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.security
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|security
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
name|SpecProvider
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
name|ValidatingJsonMap
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
name|CommandOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|admin
operator|.
name|SecurityConfHandler
operator|.
name|getListValue
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
name|handler
operator|.
name|admin
operator|.
name|SecurityConfHandler
operator|.
name|getMapValue
import|;
end_import

begin_class
DECL|class|RuleBasedAuthorizationPlugin
specifier|public
class|class
name|RuleBasedAuthorizationPlugin
implements|implements
name|AuthorizationPlugin
implements|,
name|ConfigEditablePlugin
implements|,
name|SpecProvider
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|usersVsRoles
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|usersVsRoles
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|mapping
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|WildCardSupportMap
argument_list|>
name|mapping
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|permissions
specifier|private
specifier|final
name|List
argument_list|<
name|Permission
argument_list|>
name|permissions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|class|WildCardSupportMap
specifier|private
specifier|static
class|class
name|WildCardSupportMap
extends|extends
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Permission
argument_list|>
argument_list|>
block|{
DECL|field|wildcardPrefixes
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|wildcardPrefixes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|put
specifier|public
name|List
argument_list|<
name|Permission
argument_list|>
name|put
parameter_list|(
name|String
name|key
parameter_list|,
name|List
argument_list|<
name|Permission
argument_list|>
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|!=
literal|null
operator|&&
name|key
operator|.
name|endsWith
argument_list|(
literal|"/*"
argument_list|)
condition|)
block|{
name|key
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|key
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|wildcardPrefixes
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|List
argument_list|<
name|Permission
argument_list|>
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|Permission
argument_list|>
name|result
init|=
name|super
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
operator|||
name|result
operator|!=
literal|null
condition|)
return|return
name|result
return|;
if|if
condition|(
operator|!
name|wildcardPrefixes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|wildcardPrefixes
control|)
block|{
if|if
condition|(
name|key
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|Permission
argument_list|>
name|l
init|=
name|super
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|result
operator|=
name|result
operator|==
literal|null
condition|?
operator|new
name|ArrayList
argument_list|<>
argument_list|()
else|:
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|authorize
specifier|public
name|AuthorizationResponse
name|authorize
parameter_list|(
name|AuthorizationContext
name|context
parameter_list|)
block|{
name|List
argument_list|<
name|AuthorizationContext
operator|.
name|CollectionRequest
argument_list|>
name|collectionRequests
init|=
name|context
operator|.
name|getCollectionRequests
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|getRequestType
argument_list|()
operator|==
name|AuthorizationContext
operator|.
name|RequestType
operator|.
name|ADMIN
condition|)
block|{
name|MatchStatus
name|flag
init|=
name|checkCollPerm
argument_list|(
name|mapping
operator|.
name|get
argument_list|(
literal|null
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
name|flag
operator|.
name|rsp
return|;
block|}
for|for
control|(
name|AuthorizationContext
operator|.
name|CollectionRequest
name|collreq
range|:
name|collectionRequests
control|)
block|{
comment|//check permissions for each collection
name|MatchStatus
name|flag
init|=
name|checkCollPerm
argument_list|(
name|mapping
operator|.
name|get
argument_list|(
name|collreq
operator|.
name|collectionName
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|flag
operator|!=
name|MatchStatus
operator|.
name|NO_PERMISSIONS_FOUND
condition|)
return|return
name|flag
operator|.
name|rsp
return|;
block|}
comment|//check wildcard (all=*) permissions.
name|MatchStatus
name|flag
init|=
name|checkCollPerm
argument_list|(
name|mapping
operator|.
name|get
argument_list|(
literal|"*"
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
name|flag
operator|.
name|rsp
return|;
block|}
DECL|method|checkCollPerm
specifier|private
name|MatchStatus
name|checkCollPerm
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Permission
argument_list|>
argument_list|>
name|pathVsPerms
parameter_list|,
name|AuthorizationContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|pathVsPerms
operator|==
literal|null
condition|)
return|return
name|MatchStatus
operator|.
name|NO_PERMISSIONS_FOUND
return|;
name|String
name|path
init|=
name|context
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|MatchStatus
name|flag
init|=
name|checkPathPerm
argument_list|(
name|pathVsPerms
operator|.
name|get
argument_list|(
name|path
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|flag
operator|!=
name|MatchStatus
operator|.
name|NO_PERMISSIONS_FOUND
condition|)
return|return
name|flag
return|;
return|return
name|checkPathPerm
argument_list|(
name|pathVsPerms
operator|.
name|get
argument_list|(
literal|null
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
DECL|method|checkPathPerm
specifier|private
name|MatchStatus
name|checkPathPerm
parameter_list|(
name|List
argument_list|<
name|Permission
argument_list|>
name|permissions
parameter_list|,
name|AuthorizationContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|permissions
operator|==
literal|null
operator|||
name|permissions
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|MatchStatus
operator|.
name|NO_PERMISSIONS_FOUND
return|;
name|Principal
name|principal
init|=
name|context
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
name|loopPermissions
label|:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|permissions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Permission
name|permission
init|=
name|permissions
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|PermissionNameProvider
operator|.
name|values
operator|.
name|containsKey
argument_list|(
name|permission
operator|.
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getHandler
argument_list|()
operator|instanceof
name|PermissionNameProvider
condition|)
block|{
name|PermissionNameProvider
name|handler
init|=
operator|(
name|PermissionNameProvider
operator|)
name|context
operator|.
name|getHandler
argument_list|()
decl_stmt|;
name|PermissionNameProvider
operator|.
name|Name
name|permissionName
init|=
name|handler
operator|.
name|getPermissionName
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|permissionName
operator|==
literal|null
operator|||
operator|!
name|permission
operator|.
name|name
operator|.
name|equals
argument_list|(
name|permissionName
operator|.
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
else|else
block|{
comment|//all is special. it can match any
if|if
condition|(
name|permission
operator|.
name|wellknownName
operator|!=
name|PermissionNameProvider
operator|.
name|Name
operator|.
name|ALL
condition|)
continue|continue;
block|}
block|}
else|else
block|{
if|if
condition|(
name|permission
operator|.
name|method
operator|!=
literal|null
operator|&&
operator|!
name|permission
operator|.
name|method
operator|.
name|contains
argument_list|(
name|context
operator|.
name|getHttpMethod
argument_list|()
argument_list|)
condition|)
block|{
comment|//this permissions HTTP method does not match this rule. try other rules
continue|continue;
block|}
if|if
condition|(
name|permission
operator|.
name|params
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
name|Function
argument_list|<
name|String
index|[]
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|e
range|:
name|permission
operator|.
name|params
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
index|[]
name|paramVal
init|=
name|context
operator|.
name|getParams
argument_list|()
operator|.
name|getParams
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|apply
argument_list|(
name|paramVal
argument_list|)
condition|)
continue|continue
name|loopPermissions
continue|;
block|}
block|}
block|}
if|if
condition|(
name|permission
operator|.
name|role
operator|==
literal|null
condition|)
block|{
comment|//no role is assigned permission.That means everybody is allowed to access
return|return
name|MatchStatus
operator|.
name|PERMITTED
return|;
block|}
if|if
condition|(
name|principal
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"request has come without principal. failed permission {} "
argument_list|,
name|permission
argument_list|)
expr_stmt|;
comment|//this resource needs a principal but the request has come without
comment|//any credential.
return|return
name|MatchStatus
operator|.
name|USER_REQUIRED
return|;
block|}
elseif|else
if|if
condition|(
name|permission
operator|.
name|role
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
return|return
name|MatchStatus
operator|.
name|PERMITTED
return|;
block|}
for|for
control|(
name|String
name|role
range|:
name|permission
operator|.
name|role
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|userRoles
init|=
name|usersVsRoles
operator|.
name|get
argument_list|(
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|userRoles
operator|!=
literal|null
operator|&&
name|userRoles
operator|.
name|contains
argument_list|(
name|role
argument_list|)
condition|)
return|return
name|MatchStatus
operator|.
name|PERMITTED
return|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"This resource is configured to have a permission {}, The principal {} does not have the right role "
argument_list|,
name|permission
argument_list|,
name|principal
argument_list|)
expr_stmt|;
return|return
name|MatchStatus
operator|.
name|FORBIDDEN
return|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"No permissions configured for the resource {} . So allowed to access"
argument_list|,
name|context
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|MatchStatus
operator|.
name|NO_PERMISSIONS_FOUND
return|;
block|}
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|initInfo
parameter_list|)
block|{
name|mapping
operator|.
name|put
argument_list|(
literal|null
argument_list|,
operator|new
name|WildCardSupportMap
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|getMapValue
argument_list|(
name|initInfo
argument_list|,
literal|"user-role"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|map
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
name|String
name|roleName
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|usersVsRoles
operator|.
name|put
argument_list|(
name|roleName
argument_list|,
name|Permission
operator|.
name|readValueAsSet
argument_list|(
name|map
argument_list|,
name|roleName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Map
argument_list|>
name|perms
init|=
name|getListValue
argument_list|(
name|initInfo
argument_list|,
literal|"permissions"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
name|o
range|:
name|perms
control|)
block|{
name|Permission
name|p
decl_stmt|;
try|try
block|{
name|p
operator|=
name|Permission
operator|.
name|load
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exp
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Invalid permission "
argument_list|,
name|exp
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|permissions
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|add2Mapping
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|//this is to do optimized lookup of permissions for a given collection/path
DECL|method|add2Mapping
specifier|private
name|void
name|add2Mapping
parameter_list|(
name|Permission
name|permission
parameter_list|)
block|{
for|for
control|(
name|String
name|c
range|:
name|permission
operator|.
name|collections
control|)
block|{
name|WildCardSupportMap
name|m
init|=
name|mapping
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|mapping
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|m
operator|=
operator|new
name|WildCardSupportMap
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|permission
operator|.
name|path
control|)
block|{
name|List
argument_list|<
name|Permission
argument_list|>
name|perms
init|=
name|m
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|perms
operator|==
literal|null
condition|)
name|m
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|perms
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
name|perms
operator|.
name|add
argument_list|(
name|permission
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{ }
DECL|enum|MatchStatus
enum|enum
name|MatchStatus
block|{
DECL|enum constant|USER_REQUIRED
name|USER_REQUIRED
parameter_list|(
name|AuthorizationResponse
operator|.
name|PROMPT
parameter_list|)
operator|,
DECL|enum constant|NO_PERMISSIONS_FOUND
constructor|NO_PERMISSIONS_FOUND(AuthorizationResponse.OK
block|)
enum|,
DECL|enum constant|PERMITTED
name|PERMITTED
parameter_list|(
name|AuthorizationResponse
operator|.
name|OK
parameter_list|)
operator|,
DECL|enum constant|FORBIDDEN
constructor|FORBIDDEN(AuthorizationResponse.FORBIDDEN
block|)
class|;
end_class

begin_decl_stmt
DECL|field|rsp
specifier|final
name|AuthorizationResponse
name|rsp
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|MatchStatus
name|MatchStatus
argument_list|(
name|AuthorizationResponse
name|rsp
argument_list|)
block|{
name|this
operator|.
name|rsp
operator|=
name|rsp
block|;     }
end_expr_stmt

begin_function
unit|}      @
name|Override
DECL|method|edit
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|edit
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|latestConf
parameter_list|,
name|List
argument_list|<
name|CommandOperation
argument_list|>
name|commands
parameter_list|)
block|{
for|for
control|(
name|CommandOperation
name|op
range|:
name|commands
control|)
block|{
name|AutorizationEditOperation
name|operation
init|=
name|ops
operator|.
name|get
argument_list|(
name|op
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|operation
operator|==
literal|null
condition|)
block|{
name|op
operator|.
name|unknownOperation
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
name|latestConf
operator|=
name|operation
operator|.
name|edit
argument_list|(
name|latestConf
argument_list|,
name|op
argument_list|)
expr_stmt|;
if|if
condition|(
name|latestConf
operator|==
literal|null
condition|)
return|return
literal|null
return|;
block|}
return|return
name|latestConf
return|;
block|}
end_function

begin_decl_stmt
DECL|field|ops
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AutorizationEditOperation
argument_list|>
name|ops
init|=
name|unmodifiableMap
argument_list|(
name|asList
argument_list|(
name|AutorizationEditOperation
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
name|AutorizationEditOperation
operator|::
name|getOperationName
argument_list|,
name|identity
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
end_decl_stmt

begin_function
annotation|@
name|Override
DECL|method|getSpec
specifier|public
name|ValidatingJsonMap
name|getSpec
parameter_list|()
block|{
return|return
name|Utils
operator|.
name|getSpec
argument_list|(
literal|"cluster.security.RuleBasedAuthorization"
argument_list|)
operator|.
name|getSpec
argument_list|()
return|;
block|}
end_function

unit|}
end_unit

