begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.benchmark.byTask.tasks
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|tasks
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|benchmark
operator|.
name|byTask
operator|.
name|PerfRunData
import|;
end_import

begin_comment
comment|/**  * Set a {@link java.util.Locale} for use in benchmarking.  *<p>  * Locales can be specified in the following ways:  *<ul>  *<li><code>de</code>: Language "de"  *<li><code>en,US</code>: Language "en", country "US"  *<li><code>no,NO,NY</code>: Language "no", country "NO", variant "NY"   *<li><code>ROOT</code>: The root (language-agnostic) Locale  *<li>&lt;empty string&gt;: Erase the Locale (null)  *</ul>  *</p>  */
end_comment

begin_class
DECL|class|NewLocaleTask
specifier|public
class|class
name|NewLocaleTask
extends|extends
name|PerfTask
block|{
DECL|field|language
specifier|private
name|String
name|language
decl_stmt|;
DECL|field|country
specifier|private
name|String
name|country
decl_stmt|;
DECL|field|variant
specifier|private
name|String
name|variant
decl_stmt|;
comment|/**    * Create a new {@link java.util.Locale} and set it it in the getRunData() for    * use by all future tasks.    */
DECL|method|NewLocaleTask
specifier|public
name|NewLocaleTask
parameter_list|(
name|PerfRunData
name|runData
parameter_list|)
block|{
name|super
argument_list|(
name|runData
argument_list|)
expr_stmt|;
block|}
DECL|method|createLocale
specifier|static
name|Locale
name|createLocale
parameter_list|(
name|String
name|language
parameter_list|,
name|String
name|country
parameter_list|,
name|String
name|variant
parameter_list|)
block|{
if|if
condition|(
name|language
operator|==
literal|null
operator|||
name|language
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|String
name|lang
init|=
name|language
decl_stmt|;
if|if
condition|(
name|lang
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ROOT"
argument_list|)
condition|)
name|lang
operator|=
literal|""
expr_stmt|;
comment|// empty language is the root locale in the JDK
return|return
operator|new
name|Locale
argument_list|(
name|lang
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doLogic
specifier|public
name|int
name|doLogic
parameter_list|()
throws|throws
name|Exception
block|{
name|Locale
name|locale
init|=
name|createLocale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
decl_stmt|;
name|getRunData
argument_list|()
operator|.
name|setLocale
argument_list|(
name|locale
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Changed Locale to: "
operator|+
operator|(
name|locale
operator|==
literal|null
condition|?
literal|"null"
else|:
operator|(
name|locale
operator|.
name|getDisplayName
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|?
literal|"root locale"
else|:
name|locale
operator|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|setParams
specifier|public
name|void
name|setParams
parameter_list|(
name|String
name|params
parameter_list|)
block|{
name|super
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|language
operator|=
name|country
operator|=
name|variant
operator|=
literal|""
expr_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|params
argument_list|,
literal|","
argument_list|)
decl_stmt|;
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
name|language
operator|=
name|st
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
name|country
operator|=
name|st
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
name|variant
operator|=
name|st
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsParams
specifier|public
name|boolean
name|supportsParams
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

