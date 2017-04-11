begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.metrics.reporters
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|metrics
operator|.
name|reporters
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|ObjectNameFactory
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
name|metrics
operator|.
name|SolrMetricInfo
import|;
end_import

begin_comment
comment|/**  * Factory to create MBean names for a given metric.  */
end_comment

begin_class
DECL|class|JmxObjectNameFactory
specifier|public
class|class
name|JmxObjectNameFactory
implements|implements
name|ObjectNameFactory
block|{
DECL|field|domain
specifier|private
specifier|final
name|String
name|domain
decl_stmt|;
DECL|field|subdomains
specifier|private
specifier|final
name|String
index|[]
name|subdomains
decl_stmt|;
DECL|field|reporterName
specifier|private
specifier|final
name|String
name|reporterName
decl_stmt|;
DECL|field|props
specifier|private
specifier|final
name|String
index|[]
name|props
decl_stmt|;
comment|/**    * Create ObjectName factory.    * @param reporterName name of the reporter    * @param domain JMX domain name    * @param additionalProperties additional properties as key, value pairs.    */
DECL|method|JmxObjectNameFactory
specifier|public
name|JmxObjectNameFactory
parameter_list|(
name|String
name|reporterName
parameter_list|,
name|String
name|domain
parameter_list|,
name|String
modifier|...
name|additionalProperties
parameter_list|)
block|{
name|this
operator|.
name|reporterName
operator|=
name|reporterName
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|this
operator|.
name|domain
operator|=
name|domain
expr_stmt|;
name|this
operator|.
name|subdomains
operator|=
name|domain
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
expr_stmt|;
if|if
condition|(
name|additionalProperties
operator|!=
literal|null
operator|&&
operator|(
name|additionalProperties
operator|.
name|length
operator|%
literal|2
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"additionalProperties length must be even: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|additionalProperties
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|props
operator|=
name|additionalProperties
expr_stmt|;
block|}
comment|/**    * Return current domain.    */
DECL|method|getDomain
specifier|public
name|String
name|getDomain
parameter_list|()
block|{
return|return
name|domain
return|;
block|}
comment|/**    * Return current reporterName.    */
DECL|method|getReporterName
specifier|public
name|String
name|getReporterName
parameter_list|()
block|{
return|return
name|reporterName
return|;
block|}
comment|/**    * Create a hierarchical name.    *    * @param type    metric class, eg. "counters", may be null for non-metric MBeans    * @param currentDomain  JMX domain    * @param name    object name    */
annotation|@
name|Override
DECL|method|createName
specifier|public
name|ObjectName
name|createName
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|currentDomain
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|SolrMetricInfo
name|metricInfo
init|=
name|SolrMetricInfo
operator|.
name|of
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|safeName
init|=
name|metricInfo
operator|!=
literal|null
condition|?
name|metricInfo
operator|.
name|name
else|:
name|name
decl_stmt|;
name|safeName
operator|=
name|safeName
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
comment|// It turns out that ObjectName(String) mostly preserves key ordering
comment|// as specified in the constructor (except for the 'type' key that ends
comment|// up at top level) - unlike ObjectName(String, Map) constructor
comment|// that seems to have a mind of its own...
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|domain
operator|.
name|equals
argument_list|(
name|currentDomain
argument_list|)
condition|)
block|{
if|if
condition|(
name|subdomains
operator|!=
literal|null
operator|&&
name|subdomains
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// use only first segment as domain
name|sb
operator|.
name|append
argument_list|(
name|subdomains
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
comment|// use remaining segments as properties
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|subdomains
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"dom"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|subdomains
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
comment|// separate from other properties
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|currentDomain
operator|.
name|replaceAll
argument_list|(
literal|":"
argument_list|,
literal|"_"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|currentDomain
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|props
operator|!=
literal|null
operator|&&
name|props
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|boolean
name|added
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|props
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
if|if
condition|(
name|props
index|[
name|i
index|]
operator|==
literal|null
operator|||
name|props
index|[
name|i
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|props
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|null
operator|||
name|props
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|props
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|props
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|added
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|added
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|metricInfo
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"category="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|metricInfo
operator|.
name|category
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|metricInfo
operator|.
name|scope
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|",scope="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|metricInfo
operator|.
name|scope
argument_list|)
expr_stmt|;
block|}
comment|// we could also split by type, but don't call it 'type' :)
comment|// if (type != null) {
comment|//   sb.append(",class=");
comment|//   sb.append(type);
comment|// }
name|sb
operator|.
name|append
argument_list|(
literal|",name="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|safeName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// make dotted names into hierarchies
name|String
index|[]
name|path
init|=
name|safeName
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|path
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|path
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
comment|// split by type
comment|// if (type != null) {
comment|//   sb.append("class=");
comment|//   sb.append(type);
comment|// }
name|sb
operator|.
name|append
argument_list|(
literal|"name="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|path
index|[
name|path
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|ObjectName
name|objectName
decl_stmt|;
try|try
block|{
name|objectName
operator|=
operator|new
name|ObjectName
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedObjectNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|objectName
return|;
block|}
block|}
end_class

end_unit

