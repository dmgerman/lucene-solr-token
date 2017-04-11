begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|AttributeNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|DynamicMBean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|InvalidAttributeValueException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanAttributeInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MBeanInfo
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ReflectionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenMBeanAttributeInfoSupport
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|SimpleType
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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|Map
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
name|BiConsumer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Gauge
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Metric
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
name|store
operator|.
name|AlreadyClosedException
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

begin_comment
comment|/**  * Dynamically constructed map of metrics, intentionally different from {@link com.codahale.metrics.MetricSet}  * where each metric had to be known in advance and registered separately in {@link com.codahale.metrics.MetricRegistry}.  *<p>Note: this awkwardly extends {@link Gauge} and not {@link Metric} because awkwardly {@link Metric} instances  * are not supported by {@link com.codahale.metrics.MetricRegistryListener} :(</p>  *<p>Note 2: values added to this metric map should belong to the list of types supported by JMX:  * {@link javax.management.openmbean.OpenType#ALLOWED_CLASSNAMES_LIST}, otherwise only their toString()  * representation will be shown in JConsole.</p>  */
end_comment

begin_class
DECL|class|MetricsMap
specifier|public
class|class
name|MetricsMap
implements|implements
name|Gauge
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
implements|,
name|DynamicMBean
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
comment|// set to true to use cached statistics between getMBeanInfo calls to work
comment|// around over calling getStatistics on MBeanInfos when iterating over all attributes (SOLR-6586)
DECL|field|useCachedStatsBetweenGetMBeanInfoCalls
specifier|private
specifier|final
name|boolean
name|useCachedStatsBetweenGetMBeanInfoCalls
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"useCachedStatsBetweenGetMBeanInfoCalls"
argument_list|)
decl_stmt|;
DECL|field|initializer
specifier|private
name|BiConsumer
argument_list|<
name|Boolean
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|initializer
decl_stmt|;
DECL|field|cachedValue
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cachedValue
decl_stmt|;
DECL|method|MetricsMap
specifier|public
name|MetricsMap
parameter_list|(
name|BiConsumer
argument_list|<
name|Boolean
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|initializer
parameter_list|)
block|{
name|this
operator|.
name|initializer
operator|=
name|initializer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getValue
parameter_list|()
block|{
return|return
name|getValue
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|method|getValue
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getValue
parameter_list|(
name|boolean
name|detailed
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|initializer
operator|.
name|accept
argument_list|(
name|detailed
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getAttribute
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
name|Object
name|val
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stats
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|useCachedStatsBetweenGetMBeanInfoCalls
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cachedStats
init|=
name|this
operator|.
name|cachedValue
decl_stmt|;
if|if
condition|(
name|cachedStats
operator|!=
literal|null
condition|)
block|{
name|stats
operator|=
name|cachedStats
expr_stmt|;
block|}
block|}
if|if
condition|(
name|stats
operator|==
literal|null
condition|)
block|{
name|stats
operator|=
name|getValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|val
operator|=
name|stats
operator|.
name|get
argument_list|(
name|attribute
argument_list|)
expr_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
block|{
comment|// It's String or one of the simple types, just return it as JMX suggests direct support for such types
for|for
control|(
name|String
name|simpleTypeName
range|:
name|SimpleType
operator|.
name|ALLOWED_CLASSNAMES_LIST
control|)
block|{
if|if
condition|(
name|val
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|simpleTypeName
argument_list|)
condition|)
block|{
return|return
name|val
return|;
block|}
block|}
comment|// It's an arbitrary object which could be something complex and odd, return its toString, assuming that is
comment|// a workable representation of the object
return|return
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setAttribute
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Attribute
name|attribute
parameter_list|)
throws|throws
name|AttributeNotFoundException
throws|,
name|InvalidAttributeValueException
throws|,
name|MBeanException
throws|,
name|ReflectionException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Operation not Supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getAttributes
specifier|public
name|AttributeList
name|getAttributes
parameter_list|(
name|String
index|[]
name|attributes
parameter_list|)
block|{
name|AttributeList
name|list
init|=
operator|new
name|AttributeList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|attribute
range|:
name|attributes
control|)
block|{
try|try
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Attribute
argument_list|(
name|attribute
argument_list|,
name|getAttribute
argument_list|(
name|attribute
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Could not get attribute "
operator|+
name|attribute
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|setAttributes
specifier|public
name|AttributeList
name|setAttributes
parameter_list|(
name|AttributeList
name|attributes
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Operation not Supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|invoke
specifier|public
name|Object
name|invoke
parameter_list|(
name|String
name|actionName
parameter_list|,
name|Object
index|[]
name|params
parameter_list|,
name|String
index|[]
name|signature
parameter_list|)
throws|throws
name|MBeanException
throws|,
name|ReflectionException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Operation not Supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getMBeanInfo
specifier|public
name|MBeanInfo
name|getMBeanInfo
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|MBeanAttributeInfo
argument_list|>
name|attrInfoList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|stats
init|=
name|getValue
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|useCachedStatsBetweenGetMBeanInfoCalls
condition|)
block|{
name|cachedValue
operator|=
name|stats
expr_stmt|;
block|}
try|try
block|{
name|stats
operator|.
name|forEach
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
block|{
name|Class
name|type
init|=
name|v
operator|.
name|getClass
argument_list|()
decl_stmt|;
name|OpenType
name|typeBox
init|=
name|determineType
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|||
name|typeBox
operator|==
literal|null
condition|)
block|{
name|attrInfoList
operator|.
name|add
argument_list|(
operator|new
name|MBeanAttributeInfo
argument_list|(
name|k
argument_list|,
name|String
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attrInfoList
operator|.
name|add
argument_list|(
operator|new
name|OpenMBeanAttributeInfoSupport
argument_list|(
name|k
argument_list|,
name|k
argument_list|,
name|typeBox
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// don't log issue if the core is closing
if|if
condition|(
operator|!
operator|(
name|SolrException
operator|.
name|getRootCause
argument_list|(
name|e
argument_list|)
operator|instanceof
name|AlreadyClosedException
operator|)
condition|)
name|log
operator|.
name|warn
argument_list|(
literal|"Could not get attributes of MetricsMap: {}"
argument_list|,
name|this
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|MBeanAttributeInfo
index|[]
name|attrInfoArr
init|=
name|attrInfoList
operator|.
name|toArray
argument_list|(
operator|new
name|MBeanAttributeInfo
index|[
name|attrInfoList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|MBeanInfo
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
literal|"MetricsMap"
argument_list|,
name|attrInfoArr
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|determineType
specifier|private
name|OpenType
name|determineType
parameter_list|(
name|Class
name|type
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|Field
name|field
range|:
name|SimpleType
operator|.
name|class
operator|.
name|getFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|SimpleType
operator|.
name|class
argument_list|)
condition|)
block|{
name|SimpleType
name|candidate
init|=
operator|(
name|SimpleType
operator|)
name|field
operator|.
name|get
argument_list|(
name|SimpleType
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|candidate
operator|.
name|getTypeName
argument_list|()
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|candidate
return|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

