begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
package|;
end_package

begin_comment
comment|/**  * A feature logger that logs in csv format.  */
end_comment

begin_class
DECL|class|CSVFeatureLogger
specifier|public
class|class
name|CSVFeatureLogger
extends|extends
name|FeatureLogger
block|{
DECL|field|DEFAULT_KEY_VALUE_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|DEFAULT_KEY_VALUE_SEPARATOR
init|=
literal|'='
decl_stmt|;
DECL|field|DEFAULT_FEATURE_SEPARATOR
specifier|public
specifier|static
specifier|final
name|char
name|DEFAULT_FEATURE_SEPARATOR
init|=
literal|','
decl_stmt|;
DECL|field|keyValueSep
specifier|private
specifier|final
name|char
name|keyValueSep
decl_stmt|;
DECL|field|featureSep
specifier|private
specifier|final
name|char
name|featureSep
decl_stmt|;
DECL|method|CSVFeatureLogger
specifier|public
name|CSVFeatureLogger
parameter_list|(
name|String
name|fvCacheName
parameter_list|,
name|FeatureFormat
name|f
parameter_list|)
block|{
name|super
argument_list|(
name|fvCacheName
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyValueSep
operator|=
name|DEFAULT_KEY_VALUE_SEPARATOR
expr_stmt|;
name|this
operator|.
name|featureSep
operator|=
name|DEFAULT_FEATURE_SEPARATOR
expr_stmt|;
block|}
DECL|method|CSVFeatureLogger
specifier|public
name|CSVFeatureLogger
parameter_list|(
name|String
name|fvCacheName
parameter_list|,
name|FeatureFormat
name|f
parameter_list|,
name|char
name|keyValueSep
parameter_list|,
name|char
name|featureSep
parameter_list|)
block|{
name|super
argument_list|(
name|fvCacheName
argument_list|,
name|f
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyValueSep
operator|=
name|keyValueSep
expr_stmt|;
name|this
operator|.
name|featureSep
operator|=
name|featureSep
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|makeFeatureVector
specifier|public
name|String
name|makeFeatureVector
parameter_list|(
name|LTRScoringQuery
operator|.
name|FeatureInfo
index|[]
name|featuresInfo
parameter_list|)
block|{
comment|// Allocate the buffer to a size based on the number of features instead of the
comment|// default 16.  You need space for the name, value, and two separators per feature,
comment|// but not all the features are expected to fire, so this is just a naive estimate.
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|featuresInfo
operator|.
name|length
operator|*
literal|3
argument_list|)
decl_stmt|;
name|boolean
name|isDense
init|=
name|featureFormat
operator|.
name|equals
argument_list|(
name|FeatureFormat
operator|.
name|DENSE
argument_list|)
decl_stmt|;
for|for
control|(
name|LTRScoringQuery
operator|.
name|FeatureInfo
name|featInfo
range|:
name|featuresInfo
control|)
block|{
if|if
condition|(
name|featInfo
operator|.
name|isUsed
argument_list|()
operator|||
name|isDense
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|featInfo
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|keyValueSep
argument_list|)
operator|.
name|append
argument_list|(
name|featInfo
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|featureSep
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|String
name|features
init|=
operator|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|sb
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
else|:
literal|""
operator|)
decl_stmt|;
return|return
name|features
return|;
block|}
block|}
end_class

end_unit

