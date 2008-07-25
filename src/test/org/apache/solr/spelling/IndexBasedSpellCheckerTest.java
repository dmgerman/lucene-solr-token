begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|analysis
operator|.
name|Token
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
name|analysis
operator|.
name|WhitespaceAnalyzer
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
name|document
operator|.
name|Document
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|IndexWriter
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
name|spell
operator|.
name|JaroWinklerDistance
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
name|spell
operator|.
name|SpellChecker
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
name|spell
operator|.
name|StringDistance
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
name|util
operator|.
name|AbstractSolrTestCase
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
name|util
operator|.
name|RefCounted
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
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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

begin_comment
comment|/**  * @since solr 1.3  */
end_comment

begin_class
DECL|class|IndexBasedSpellCheckerTest
specifier|public
class|class
name|IndexBasedSpellCheckerTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|field|queryConverter
specifier|protected
name|SpellingQueryConverter
name|queryConverter
decl_stmt|;
DECL|field|DOCS
specifier|protected
specifier|static
name|String
index|[]
name|DOCS
init|=
operator|new
name|String
index|[]
block|{
literal|"This is a title"
block|,
literal|"The quick reb fox jumped over the lazy brown dogs."
block|,
literal|"This is a document"
block|,
literal|"another document"
block|,
literal|"red fox"
block|,
literal|"green bun"
block|,
literal|"green bud"
block|}
decl_stmt|;
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|//Index something with a title
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|DOCS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"title"
argument_list|,
name|DOCS
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertU
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|allq
init|=
literal|"id:[0 TO 3]"
decl_stmt|;
name|assertQ
argument_list|(
literal|"docs not added"
argument_list|,
name|req
argument_list|(
name|allq
argument_list|)
argument_list|)
expr_stmt|;
name|queryConverter
operator|=
operator|new
name|SimpleQueryConverter
argument_list|()
expr_stmt|;
block|}
DECL|method|testSpelling
specifier|public
name|void
name|testSpelling
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexBasedSpellChecker
name|checker
init|=
operator|new
name|IndexBasedSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
literal|"classname"
argument_list|,
name|IndexBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"spellingIdx"
operator|+
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|INDEX_DIR
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|IndexBasedSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|SPELLCHECKER_ARG_NAME
argument_list|,
name|spellchecker
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|dictName
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dictName
operator|+
literal|" is not equal to "
operator|+
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|,
name|dictName
operator|.
name|equals
argument_list|(
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|holder
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|holder
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"documemt"
argument_list|)
decl_stmt|;
name|SpellingResult
name|result
init|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//should be lowercased, b/c we are using a lowercasing analyzer
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suggestions
init|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"documemt is null and it shouldn't be"
argument_list|,
name|suggestions
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"documemt Size: "
operator|+
name|suggestions
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
init|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"document"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"document"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|)
expr_stmt|;
comment|//test something not in the spell checker
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"super"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|//test something that is spelled correctly
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"document"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is null and it shouldn't be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|//Has multiple possibilities, but the exact exists, so that should be returned
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"red"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|//Try out something which should have multiple suggestions
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"bug"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is null and it shouldn't be"
argument_list|,
name|suggestions
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions Size: "
operator|+
name|suggestions
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|2
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|entry
operator|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is equal to "
operator|+
literal|"bug and it shouldn't be"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"bug"
argument_list|)
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|)
expr_stmt|;
name|entry
operator|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is equal to "
operator|+
literal|"bug and it shouldn't be"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"bug"
argument_list|)
operator|==
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|SpellingResult
operator|.
name|NO_FREQUENCY_INFO
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|holder
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testExtendedResults
specifier|public
name|void
name|testExtendedResults
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexBasedSpellChecker
name|checker
init|=
operator|new
name|IndexBasedSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
literal|"classname"
argument_list|,
name|IndexBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"spellingIdx"
operator|+
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|INDEX_DIR
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|IndexBasedSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|SPELLCHECKER_ARG_NAME
argument_list|,
name|spellchecker
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|dictName
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dictName
operator|+
literal|" is not equal to "
operator|+
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|,
name|dictName
operator|.
name|equals
argument_list|(
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|holder
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|holder
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"documemt"
argument_list|)
decl_stmt|;
name|SpellingResult
name|result
init|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//should be lowercased, b/c we are using a lowercasing analyzer
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suggestions
init|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"documemt is null and it shouldn't be"
argument_list|,
name|suggestions
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"documemt Size: "
operator|+
name|suggestions
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
init|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"document"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"document"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|//test something not in the spell checker
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"super"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"document"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|holder
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|TestSpellChecker
specifier|private
class|class
name|TestSpellChecker
extends|extends
name|IndexBasedSpellChecker
block|{
DECL|method|getSpellChecker
specifier|public
name|SpellChecker
name|getSpellChecker
parameter_list|()
block|{
return|return
name|spellChecker
return|;
block|}
block|}
DECL|method|testAlternateDistance
specifier|public
name|void
name|testAlternateDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|TestSpellChecker
name|checker
init|=
operator|new
name|TestSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
literal|"classname"
argument_list|,
name|IndexBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"spellingIdx"
operator|+
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|INDEX_DIR
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|IndexBasedSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|SPELLCHECKER_ARG_NAME
argument_list|,
name|spellchecker
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|STRING_DISTANCE
argument_list|,
name|JaroWinklerDistance
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|dictName
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dictName
operator|+
literal|" is not equal to "
operator|+
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|,
name|dictName
operator|.
name|equals
argument_list|(
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|holder
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|holder
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|SpellChecker
name|sc
init|=
name|checker
operator|.
name|getSpellChecker
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sc is null and it shouldn't be"
argument_list|,
name|sc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|StringDistance
name|sd
init|=
name|sc
operator|.
name|getStringDistance
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"sd is null and it shouldn't be"
argument_list|,
name|sd
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"sd is not an instance of "
operator|+
name|JaroWinklerDistance
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|sd
operator|instanceof
name|JaroWinklerDistance
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|holder
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testAlternateLocation
specifier|public
name|void
name|testAlternateLocation
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|ALT_DOCS
init|=
operator|new
name|String
index|[]
block|{
literal|"jumpin jack flash"
block|,
literal|"Sargent Peppers Lonely Hearts Club Band"
block|,
literal|"Born to Run"
block|,
literal|"Thunder Road"
block|,
literal|"Londons Burning"
block|,
literal|"A Horse with No Name"
block|,
literal|"Sweet Caroline"
block|}
decl_stmt|;
name|IndexBasedSpellChecker
name|checker
init|=
operator|new
name|IndexBasedSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
name|spellchecker
init|=
operator|new
name|NamedList
argument_list|()
decl_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
literal|"classname"
argument_list|,
name|IndexBasedSpellChecker
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|indexDir
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"spellingIdx"
operator|+
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
comment|//create a standalone index
name|File
name|altIndexDir
init|=
operator|new
name|File
argument_list|(
name|tmpDir
argument_list|,
literal|"alternateIdx"
operator|+
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|altIndexDir
argument_list|,
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|,
name|IndexWriter
operator|.
name|MaxFieldLength
operator|.
name|LIMITED
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
name|ALT_DOCS
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"title"
argument_list|,
name|ALT_DOCS
index|[
name|i
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|TOKENIZED
argument_list|)
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|optimize
argument_list|()
expr_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|INDEX_DIR
argument_list|,
name|indexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|LOCATION
argument_list|,
name|altIndexDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|IndexBasedSpellChecker
operator|.
name|FIELD
argument_list|,
literal|"title"
argument_list|)
expr_stmt|;
name|spellchecker
operator|.
name|add
argument_list|(
name|AbstractLuceneSpellChecker
operator|.
name|SPELLCHECKER_ARG_NAME
argument_list|,
name|spellchecker
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|String
name|dictName
init|=
name|checker
operator|.
name|init
argument_list|(
name|spellchecker
argument_list|,
name|core
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|dictName
operator|+
literal|" is not equal to "
operator|+
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|,
name|dictName
operator|.
name|equals
argument_list|(
name|SolrSpellChecker
operator|.
name|DEFAULT_DICTIONARY_NAME
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|holder
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|searcher
init|=
name|holder
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|checker
operator|.
name|build
argument_list|(
name|core
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"flesh"
argument_list|)
decl_stmt|;
name|SpellingResult
name|result
init|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
comment|//should be lowercased, b/c we are using a lowercasing analyzer
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|suggestions
init|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"flesh is null and it shouldn't be"
argument_list|,
name|suggestions
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"flesh Size: "
operator|+
name|suggestions
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
literal|1
argument_list|,
name|suggestions
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
init|=
name|suggestions
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not equal to "
operator|+
literal|"flash"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"flash"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
comment|//test something not in the spell checker
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"super"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
name|tokens
operator|=
name|queryConverter
operator|.
name|convert
argument_list|(
literal|"Caroline"
argument_list|)
expr_stmt|;
name|result
operator|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|tokens
argument_list|,
name|reader
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"result is null and it shouldn't be"
argument_list|,
name|result
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|suggestions
operator|=
name|result
operator|.
name|get
argument_list|(
name|tokens
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"suggestions is not null and it should be"
argument_list|,
name|suggestions
operator|==
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|holder
operator|.
name|decref
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

