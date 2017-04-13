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
name|WeakHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Layout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|ThrowableInformation
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
name|cloud
operator|.
name|ZkController
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
name|SolrException
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
name|StringUtils
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
name|cloud
operator|.
name|DocCollection
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
name|cloud
operator|.
name|Replica
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
name|SuppressForbidden
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
name|core
operator|.
name|SolrCore
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
name|SolrQueryRequest
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
name|SolrRequestInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|MDC
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
name|ZkStateReader
operator|.
name|COLLECTION_PROP
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
name|ZkStateReader
operator|.
name|CORE_NAME_PROP
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
name|ZkStateReader
operator|.
name|NODE_NAME_PROP
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
name|ZkStateReader
operator|.
name|REPLICA_PROP
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
name|ZkStateReader
operator|.
name|SHARD_ID_PROP
import|;
end_import

begin_class
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"class is specific to log4j"
argument_list|)
DECL|class|SolrLogLayout
specifier|public
class|class
name|SolrLogLayout
extends|extends
name|Layout
block|{
comment|/**    * Add this interface to a thread group and the string returned by getTag()    * will appear in log statements of any threads under that group.    */
DECL|interface|TG
specifier|public
specifier|static
interface|interface
name|TG
block|{
DECL|method|getTag
specifier|public
name|String
name|getTag
parameter_list|()
function_decl|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Need currentTimeMillis to compare against log event timestamp. "
operator|+
literal|"This is inaccurate but unavoidable due to interface limitations, in any case this is just for logging."
argument_list|)
DECL|field|startTime
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|lastTime
name|long
name|lastTime
init|=
name|startTime
decl_stmt|;
DECL|field|methodAlias
name|Map
argument_list|<
name|Method
argument_list|,
name|String
argument_list|>
name|methodAlias
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|class|Method
specifier|public
specifier|static
class|class
name|Method
block|{
DECL|field|className
specifier|public
name|String
name|className
decl_stmt|;
DECL|field|methodName
specifier|public
name|String
name|methodName
decl_stmt|;
DECL|method|Method
specifier|public
name|Method
parameter_list|(
name|String
name|className
parameter_list|,
name|String
name|methodName
parameter_list|)
block|{
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
name|this
operator|.
name|methodName
operator|=
name|methodName
expr_stmt|;
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
name|className
operator|.
name|hashCode
argument_list|()
operator|+
name|methodName
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|Method
operator|)
condition|)
return|return
literal|false
return|;
name|Method
name|other
init|=
operator|(
name|Method
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|className
operator|.
name|equals
argument_list|(
name|other
operator|.
name|className
argument_list|)
operator|&&
name|methodName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|methodName
argument_list|)
operator|)
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
name|className
operator|+
literal|'.'
operator|+
name|methodName
return|;
block|}
block|}
DECL|class|CoreInfo
specifier|public
specifier|static
class|class
name|CoreInfo
block|{
DECL|field|maxCoreNum
specifier|static
name|int
name|maxCoreNum
decl_stmt|;
DECL|field|shortId
name|String
name|shortId
decl_stmt|;
DECL|field|url
name|String
name|url
decl_stmt|;
DECL|field|coreProps
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|coreProps
decl_stmt|;
block|}
DECL|field|coreInfoMap
name|Map
argument_list|<
name|Integer
argument_list|,
name|CoreInfo
argument_list|>
name|coreInfoMap
init|=
operator|new
name|WeakHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|classAliases
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|classAliases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|appendThread
specifier|public
name|void
name|appendThread
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|LoggingEvent
name|event
parameter_list|)
block|{
name|Thread
name|th
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
comment|/******      * sb.append(" T="); sb.append(th.getName()).append(' ');      *       * // NOTE: tried creating a thread group around jetty but we seem to lose      * it and request // threads are in the normal "main" thread group      * ThreadGroup tg = th.getThreadGroup(); while (tg != null) {      * sb.append("(group_name=").append(tg.getName()).append(")");      *       * if (tg instanceof TG) { sb.append(((TG)tg).getTag()); sb.append('/'); }      * try { tg = tg.getParent(); } catch (Throwable e) { tg = null; } }      ******/
comment|// NOTE: LogRecord.getThreadID is *not* equal to Thread.getId()
name|sb
operator|.
name|append
argument_list|(
literal|" T"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|th
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
return|return
name|_format
argument_list|(
name|event
argument_list|)
return|;
block|}
DECL|method|_format
specifier|public
name|String
name|_format
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
name|String
name|message
init|=
operator|(
name|String
operator|)
name|event
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|message
operator|=
literal|""
expr_stmt|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|message
operator|.
name|length
argument_list|()
operator|+
literal|80
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|event
operator|.
name|timeStamp
decl_stmt|;
name|long
name|timeFromStart
init|=
name|now
operator|-
name|startTime
decl_stmt|;
name|long
name|timeSinceLast
init|=
name|now
operator|-
name|lastTime
decl_stmt|;
name|lastTime
operator|=
name|now
expr_stmt|;
name|String
name|shortClassName
init|=
name|getShortClassName
argument_list|(
name|event
operator|.
name|getLocationInformation
argument_list|()
operator|.
name|getClassName
argument_list|()
argument_list|,
name|event
operator|.
name|getLocationInformation
argument_list|()
operator|.
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
comment|/***      * sb.append(timeFromStart).append(' ').append(timeSinceLast);      * sb.append(' ');      * sb.append(record.getSourceClassName()).append('.').append(      * record.getSourceMethodName()); sb.append(' ');      * sb.append(record.getLevel());      ***/
name|SolrRequestInfo
name|requestInfo
init|=
name|SolrRequestInfo
operator|.
name|getRequestInfo
argument_list|()
decl_stmt|;
name|SolrQueryRequest
name|req
init|=
name|requestInfo
operator|==
literal|null
condition|?
literal|null
else|:
name|requestInfo
operator|.
name|getReq
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|req
operator|==
literal|null
condition|?
literal|null
else|:
name|req
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|ZkController
name|zkController
init|=
literal|null
decl_stmt|;
name|CoreInfo
name|info
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|core
operator|!=
literal|null
condition|)
block|{
name|info
operator|=
name|coreInfoMap
operator|.
name|get
argument_list|(
name|core
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
name|info
operator|=
operator|new
name|CoreInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|shortId
operator|=
literal|"C"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|CoreInfo
operator|.
name|maxCoreNum
operator|++
argument_list|)
expr_stmt|;
name|coreInfoMap
operator|.
name|put
argument_list|(
name|core
operator|.
name|hashCode
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|"ASYNC "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" NEW_CORE "
operator|+
name|info
operator|.
name|shortId
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" name="
operator|+
name|core
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|core
argument_list|)
expr_stmt|;
block|}
name|zkController
operator|=
name|core
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|getZkController
argument_list|()
expr_stmt|;
if|if
condition|(
name|zkController
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|info
operator|.
name|url
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|url
operator|=
name|zkController
operator|.
name|getBaseUrl
argument_list|()
operator|+
literal|"/"
operator|+
name|core
operator|.
name|getName
argument_list|()
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" url="
operator|+
name|info
operator|.
name|url
operator|+
literal|" node="
operator|+
name|zkController
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|coreProps
init|=
name|getReplicaProps
argument_list|(
name|zkController
argument_list|,
name|core
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|coreProps
operator|==
literal|null
operator|||
operator|!
name|coreProps
operator|.
name|equals
argument_list|(
name|info
operator|.
name|coreProps
argument_list|)
condition|)
block|{
name|info
operator|.
name|coreProps
operator|=
name|coreProps
expr_stmt|;
specifier|final
name|String
name|corePropsString
init|=
literal|"coll:"
operator|+
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
operator|+
literal|" core:"
operator|+
name|core
operator|.
name|getName
argument_list|()
operator|+
literal|" props:"
operator|+
name|coreProps
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|info
operator|.
name|shortId
operator|+
literal|"_STATE="
operator|+
name|corePropsString
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|timeFromStart
argument_list|)
expr_stmt|;
comment|// sb.append("\nL").append(record.getSequenceNumber()); // log number is
comment|// useful for sequencing when looking at multiple parts of a log file, but
comment|// ms since start should be fine.
name|appendThread
argument_list|(
name|sb
argument_list|,
name|event
argument_list|)
expr_stmt|;
name|appendMDC
argument_list|(
name|sb
argument_list|)
expr_stmt|;
comment|// todo: should be able to get port from core container for non zk tests
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|info
operator|.
name|shortId
argument_list|)
expr_stmt|;
comment|// core
block|}
if|if
condition|(
name|shortClassName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|shortClassName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|!=
name|Level
operator|.
name|INFO
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|event
operator|.
name|getLevel
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|appendMultiLineString
argument_list|(
name|sb
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|ThrowableInformation
name|thInfo
init|=
name|event
operator|.
name|getThrowableInformation
argument_list|()
decl_stmt|;
if|if
condition|(
name|thInfo
operator|!=
literal|null
condition|)
block|{
name|Throwable
name|th
init|=
name|event
operator|.
name|getThrowableInformation
argument_list|()
operator|.
name|getThrowable
argument_list|()
decl_stmt|;
if|if
condition|(
name|th
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|String
name|err
init|=
name|SolrException
operator|.
name|toStr
argument_list|(
name|th
argument_list|)
decl_stmt|;
name|String
name|ignoredMsg
init|=
name|SolrException
operator|.
name|doIgnore
argument_list|(
name|th
argument_list|,
name|err
argument_list|)
decl_stmt|;
if|if
condition|(
name|ignoredMsg
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ignoredMsg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
comment|/***      * Isn't core specific... prob better logged from zkController if (info !=      * null) { ClusterState clusterState = zkController.getClusterState(); if      * (info.clusterState != clusterState) { // something has changed in the      * matrix... sb.append(zkController.getBaseUrl() +      * " sees new ClusterState:"); } }      ***/
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getReplicaProps
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getReplicaProps
parameter_list|(
name|ZkController
name|zkController
parameter_list|,
name|SolrCore
name|core
parameter_list|)
block|{
specifier|final
name|String
name|collectionName
init|=
name|core
operator|.
name|getCoreDescriptor
argument_list|()
operator|.
name|getCloudDescriptor
argument_list|()
operator|.
name|getCollectionName
argument_list|()
decl_stmt|;
name|DocCollection
name|collection
init|=
name|zkController
operator|.
name|getClusterState
argument_list|()
operator|.
name|getCollectionOrNull
argument_list|(
name|collectionName
argument_list|)
decl_stmt|;
name|Replica
name|replica
init|=
name|collection
operator|.
name|getReplica
argument_list|(
name|zkController
operator|.
name|getCoreNodeName
argument_list|(
name|core
operator|.
name|getCoreDescriptor
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|replica
operator|!=
literal|null
condition|)
block|{
return|return
name|replica
operator|.
name|getProperties
argument_list|()
return|;
block|}
return|return
name|Collections
operator|.
name|EMPTY_MAP
return|;
block|}
DECL|method|addFirstLine
specifier|private
name|void
name|addFirstLine
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
comment|// INFO: [] webapp=/solr path=/select params={q=foobarbaz} hits=0 status=0
comment|// QTime=1
if|if
condition|(
operator|!
name|shorterFormat
operator|||
operator|!
name|msg
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|idx
init|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|']'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
operator|||
operator|!
name|msg
operator|.
name|startsWith
argument_list|(
literal|" webapp="
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|,
name|idx
operator|+
literal|8
argument_list|)
expr_stmt|;
comment|// space after webapp=
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// = in path=
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|idx2
init|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx2
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
name|idx2
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// path
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|"params="
argument_list|,
name|idx2
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|idx2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|appendMultiLineString
specifier|private
name|void
name|appendMultiLineString
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|int
name|idx
init|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|addFirstLine
argument_list|(
name|sb
argument_list|,
name|msg
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|lastIdx
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|lastIdx
operator|==
operator|-
literal|1
condition|)
block|{
name|addFirstLine
argument_list|(
name|sb
argument_list|,
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|lastIdx
operator|==
operator|-
literal|1
condition|)
block|{
name|addFirstLine
argument_list|(
name|sb
argument_list|,
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|,
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|msg
operator|.
name|substring
argument_list|(
name|lastIdx
operator|+
literal|1
argument_list|,
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n\t"
argument_list|)
expr_stmt|;
name|lastIdx
operator|=
name|idx
expr_stmt|;
name|idx
operator|=
name|msg
operator|.
name|indexOf
argument_list|(
literal|'\n'
argument_list|,
name|lastIdx
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: name this better... it's only for cloud tests where every core
comment|// container has just one solr server so Port/Core are fine
DECL|field|shorterFormat
specifier|public
name|boolean
name|shorterFormat
init|=
literal|false
decl_stmt|;
DECL|method|setShorterFormat
specifier|public
name|void
name|setShorterFormat
parameter_list|()
block|{
name|shorterFormat
operator|=
literal|true
expr_stmt|;
comment|// looking at /update is enough... we don't need "UPDATE /update"
name|methodAlias
operator|.
name|put
argument_list|(
operator|new
name|Method
argument_list|(
literal|"org.apache.solr.update.processor.LogUpdateProcessor"
argument_list|,
literal|"finish"
argument_list|)
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|field|classAndMethod
specifier|private
name|Method
name|classAndMethod
init|=
operator|new
name|Method
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// don't need to be
comment|// thread safe
DECL|method|getShortClassName
specifier|private
name|String
name|getShortClassName
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|method
parameter_list|)
block|{
name|classAndMethod
operator|.
name|className
operator|=
name|name
expr_stmt|;
name|classAndMethod
operator|.
name|methodName
operator|=
name|method
expr_stmt|;
name|String
name|out
init|=
name|methodAlias
operator|.
name|get
argument_list|(
name|classAndMethod
argument_list|)
decl_stmt|;
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
return|return
name|out
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|lastDot
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDot
operator|<
literal|0
condition|)
return|return
name|name
operator|+
literal|'.'
operator|+
name|method
return|;
name|int
name|prevIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|char
name|ch
init|=
name|name
operator|.
name|charAt
argument_list|(
name|prevIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
name|name
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|,
name|prevIndex
operator|+
literal|1
argument_list|)
decl_stmt|;
name|ch
operator|=
name|name
operator|.
name|charAt
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|>=
name|lastDot
operator|||
name|Character
operator|.
name|isUpperCase
argument_list|(
name|ch
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|prevIndex
operator|=
name|idx
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|'.'
operator|+
name|method
return|;
block|}
annotation|@
name|Override
DECL|method|activateOptions
specifier|public
name|void
name|activateOptions
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|ignoresThrowable
specifier|public
name|boolean
name|ignoresThrowable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|appendMDC
specifier|private
name|void
name|appendMDC
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|NODE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" n:"
argument_list|)
operator|.
name|append
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|NODE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|COLLECTION_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" c:"
argument_list|)
operator|.
name|append
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|COLLECTION_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|SHARD_ID_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" s:"
argument_list|)
operator|.
name|append
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|SHARD_ID_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|REPLICA_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" r:"
argument_list|)
operator|.
name|append
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|REPLICA_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|CORE_NAME_PROP
argument_list|)
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|" x:"
argument_list|)
operator|.
name|append
argument_list|(
name|MDC
operator|.
name|get
argument_list|(
name|CORE_NAME_PROP
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

