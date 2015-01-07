begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.core
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|NamedList
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

begin_comment
comment|/**  * An Object which represents a {@code<initParams>} tag  */
end_comment

begin_class
DECL|class|InitParams
specifier|public
class|class
name|InitParams
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"initParams"
decl_stmt|;
DECL|field|name
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|paths
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
DECL|field|defaults
DECL|field|invariants
DECL|field|appends
specifier|public
specifier|final
name|NamedList
name|defaults
decl_stmt|,
name|invariants
decl_stmt|,
name|appends
decl_stmt|;
DECL|method|InitParams
specifier|public
name|InitParams
parameter_list|(
name|PluginInfo
name|p
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|p
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
literal|null
decl_stmt|;
name|String
name|pathStr
init|=
name|p
operator|.
name|attributes
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathStr
operator|!=
literal|null
condition|)
block|{
name|paths
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|pathStr
argument_list|,
literal|','
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
name|NamedList
name|nl
init|=
operator|(
name|NamedList
operator|)
name|p
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
decl_stmt|;
name|defaults
operator|=
name|nl
operator|==
literal|null
condition|?
literal|null
else|:
name|nl
operator|.
name|getImmutableCopy
argument_list|()
expr_stmt|;
name|nl
operator|=
operator|(
name|NamedList
operator|)
name|p
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|)
expr_stmt|;
name|invariants
operator|=
name|nl
operator|==
literal|null
condition|?
literal|null
else|:
name|nl
operator|.
name|getImmutableCopy
argument_list|()
expr_stmt|;
name|nl
operator|=
operator|(
name|NamedList
operator|)
name|p
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|APPENDS
argument_list|)
expr_stmt|;
name|appends
operator|=
name|nl
operator|==
literal|null
condition|?
literal|null
else|:
name|nl
operator|.
name|getImmutableCopy
argument_list|()
expr_stmt|;
block|}
DECL|method|matchPath
specifier|public
name|boolean
name|matchPath
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|paths
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|paths
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
if|if
condition|(
name|matchPath
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|matchPath
specifier|private
specifier|static
name|boolean
name|matchPath
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|pathSplit
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|path
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nameSplit
init|=
name|StrUtils
operator|.
name|splitSmart
argument_list|(
name|name
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|nameSplit
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s
init|=
name|nameSplit
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|ps
init|=
name|pathSplit
operator|.
name|size
argument_list|()
operator|>
name|i
condition|?
name|pathSplit
operator|.
name|get
argument_list|(
name|i
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|ps
argument_list|)
condition|)
continue|continue;
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|ps
argument_list|)
operator|&&
name|nameSplit
operator|.
name|size
argument_list|()
operator|==
name|i
operator|+
literal|1
condition|)
return|return
literal|true
return|;
if|if
condition|(
literal|"**"
operator|.
name|equals
argument_list|(
name|ps
argument_list|)
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
name|String
name|ps
init|=
name|pathSplit
operator|.
name|size
argument_list|()
operator|>
name|i
condition|?
name|pathSplit
operator|.
name|get
argument_list|(
name|i
argument_list|)
else|:
literal|null
decl_stmt|;
return|return
literal|"*"
operator|.
name|equals
argument_list|(
name|ps
argument_list|)
operator|||
literal|"**"
operator|.
name|equals
argument_list|(
name|ps
argument_list|)
return|;
block|}
DECL|method|apply
specifier|public
name|void
name|apply
parameter_list|(
name|PluginInfo
name|info
parameter_list|)
block|{
if|if
condition|(
operator|!
name|info
operator|.
name|isFromSolrConfig
argument_list|()
condition|)
block|{
comment|//if this is a component implicitly defined in code it should be overridden by initPrams
name|merge
argument_list|(
name|defaults
argument_list|,
operator|(
name|NamedList
operator|)
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
argument_list|,
name|info
operator|.
name|initArgs
argument_list|,
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//if the args is initialized from solrconfig.xml inside the requesthHandler it should be taking precedence over  initParams
name|merge
argument_list|(
operator|(
name|NamedList
operator|)
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|)
argument_list|,
name|defaults
argument_list|,
name|info
operator|.
name|initArgs
argument_list|,
name|PluginInfo
operator|.
name|DEFAULTS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|merge
argument_list|(
operator|(
name|NamedList
operator|)
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|)
argument_list|,
name|invariants
argument_list|,
name|info
operator|.
name|initArgs
argument_list|,
name|PluginInfo
operator|.
name|INVARIANTS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|merge
argument_list|(
operator|(
name|NamedList
operator|)
name|info
operator|.
name|initArgs
operator|.
name|get
argument_list|(
name|PluginInfo
operator|.
name|APPENDS
argument_list|)
argument_list|,
name|appends
argument_list|,
name|info
operator|.
name|initArgs
argument_list|,
name|PluginInfo
operator|.
name|APPENDS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|merge
specifier|private
specifier|static
name|void
name|merge
parameter_list|(
name|NamedList
name|first
parameter_list|,
name|NamedList
name|second
parameter_list|,
name|NamedList
name|sink
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|appends
parameter_list|)
block|{
if|if
condition|(
name|first
operator|==
literal|null
operator|&&
name|second
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
name|first
operator|=
operator|new
name|NamedList
argument_list|()
expr_stmt|;
name|NamedList
name|nl
init|=
name|first
operator|.
name|clone
argument_list|()
decl_stmt|;
if|if
condition|(
name|appends
condition|)
block|{
if|if
condition|(
name|second
operator|!=
literal|null
condition|)
name|nl
operator|.
name|addAll
argument_list|(
name|second
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|a
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|b
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|first
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
name|a
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|second
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Object
name|o
range|:
name|second
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|o
decl_stmt|;
name|b
operator|.
name|add
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|s
range|:
name|b
control|)
block|{
if|if
condition|(
name|a
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
continue|continue;
for|for
control|(
name|Object
name|v
range|:
name|second
operator|.
name|getAll
argument_list|(
name|s
argument_list|)
control|)
name|nl
operator|.
name|add
argument_list|(
name|s
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sink
operator|.
name|indexOf
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
name|sink
operator|.
name|setVal
argument_list|(
name|sink
operator|.
name|indexOf
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sink
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|nl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

