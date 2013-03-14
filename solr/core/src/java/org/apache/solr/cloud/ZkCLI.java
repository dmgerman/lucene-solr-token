begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
package|;
end_package

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
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
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
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|CommandLineParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|HelpFormatter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|OptionBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|cli
operator|.
name|PosixParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|OnReconnect
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
name|SolrZkClient
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
name|ConfigSolr
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
name|ConfigSolrXmlBackCompat
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
name|SolrProperties
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
name|SolrResourceLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|KeeperException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_class
DECL|class|ZkCLI
specifier|public
class|class
name|ZkCLI
block|{
DECL|field|MAKEPATH
specifier|private
specifier|static
specifier|final
name|String
name|MAKEPATH
init|=
literal|"makepath"
decl_stmt|;
DECL|field|DOWNCONFIG
specifier|private
specifier|static
specifier|final
name|String
name|DOWNCONFIG
init|=
literal|"downconfig"
decl_stmt|;
DECL|field|ZK_CLI_NAME
specifier|private
specifier|static
specifier|final
name|String
name|ZK_CLI_NAME
init|=
literal|"ZkCLI"
decl_stmt|;
DECL|field|HELP
specifier|private
specifier|static
specifier|final
name|String
name|HELP
init|=
literal|"help"
decl_stmt|;
DECL|field|LINKCONFIG
specifier|private
specifier|static
specifier|final
name|String
name|LINKCONFIG
init|=
literal|"linkconfig"
decl_stmt|;
DECL|field|CONFDIR
specifier|private
specifier|static
specifier|final
name|String
name|CONFDIR
init|=
literal|"confdir"
decl_stmt|;
DECL|field|CONFNAME
specifier|private
specifier|static
specifier|final
name|String
name|CONFNAME
init|=
literal|"confname"
decl_stmt|;
DECL|field|REVERSE
specifier|private
specifier|static
specifier|final
name|String
name|REVERSE
init|=
literal|"reverse"
decl_stmt|;
DECL|field|ZKHOST
specifier|private
specifier|static
specifier|final
name|String
name|ZKHOST
init|=
literal|"zkhost"
decl_stmt|;
DECL|field|RUNZK
specifier|private
specifier|static
specifier|final
name|String
name|RUNZK
init|=
literal|"runzk"
decl_stmt|;
DECL|field|SOLRHOME
specifier|private
specifier|static
specifier|final
name|String
name|SOLRHOME
init|=
literal|"solrhome"
decl_stmt|;
DECL|field|BOOTSTRAP
specifier|private
specifier|static
specifier|final
name|String
name|BOOTSTRAP
init|=
literal|"bootstrap"
decl_stmt|;
DECL|field|SOLR_XML
specifier|private
specifier|static
specifier|final
name|String
name|SOLR_XML
init|=
literal|"solr.xml"
decl_stmt|;
DECL|field|UPCONFIG
specifier|private
specifier|static
specifier|final
name|String
name|UPCONFIG
init|=
literal|"upconfig"
decl_stmt|;
DECL|field|COLLECTION
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION
init|=
literal|"collection"
decl_stmt|;
DECL|field|CLEAR
specifier|private
specifier|static
specifier|final
name|String
name|CLEAR
init|=
literal|"clear"
decl_stmt|;
DECL|field|LIST
specifier|private
specifier|static
specifier|final
name|String
name|LIST
init|=
literal|"list"
decl_stmt|;
DECL|field|CMD
specifier|private
specifier|static
specifier|final
name|String
name|CMD
init|=
literal|"cmd"
decl_stmt|;
comment|/**    * Allows you to perform a variety of zookeeper related tasks, such as:    *     * Bootstrap the current configs for all collections in solr.xml.    *     * Upload a named config set from a given directory.    *     * Link a named config set explicity to a collection.    *     * Clear ZooKeeper info.    *     * If you also pass a solrPort, it will be used to start an embedded zk useful    * for single machine, multi node tests.    */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|KeeperException
block|{
name|CommandLineParser
name|parser
init|=
operator|new
name|PosixParser
argument_list|()
decl_stmt|;
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|OptionBuilder
operator|.
name|hasArg
argument_list|(
literal|true
argument_list|)
operator|.
name|withDescription
argument_list|(
literal|"cmd to run: "
operator|+
name|BOOTSTRAP
operator|+
literal|", "
operator|+
name|UPCONFIG
operator|+
literal|", "
operator|+
name|DOWNCONFIG
operator|+
literal|", "
operator|+
name|LINKCONFIG
operator|+
literal|", "
operator|+
name|MAKEPATH
operator|+
literal|", "
operator|+
name|LIST
operator|+
literal|", "
operator|+
name|CLEAR
argument_list|)
operator|.
name|create
argument_list|(
name|CMD
argument_list|)
argument_list|)
expr_stmt|;
name|Option
name|zkHostOption
init|=
operator|new
name|Option
argument_list|(
literal|"z"
argument_list|,
name|ZKHOST
argument_list|,
literal|true
argument_list|,
literal|"ZooKeeper host address"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|zkHostOption
argument_list|)
expr_stmt|;
name|Option
name|solrHomeOption
init|=
operator|new
name|Option
argument_list|(
literal|"s"
argument_list|,
name|SOLRHOME
argument_list|,
literal|true
argument_list|,
literal|"for "
operator|+
name|BOOTSTRAP
operator|+
literal|", "
operator|+
name|RUNZK
operator|+
literal|": solrhome location"
argument_list|)
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|zkHostOption
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
name|solrHomeOption
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"d"
argument_list|,
name|CONFDIR
argument_list|,
literal|true
argument_list|,
literal|"for "
operator|+
name|UPCONFIG
operator|+
literal|": a directory of configuration files"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"n"
argument_list|,
name|CONFNAME
argument_list|,
literal|true
argument_list|,
literal|"for "
operator|+
name|UPCONFIG
operator|+
literal|", "
operator|+
name|LINKCONFIG
operator|+
literal|": name of the config set"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"c"
argument_list|,
name|COLLECTION
argument_list|,
literal|true
argument_list|,
literal|"for "
operator|+
name|LINKCONFIG
operator|+
literal|": name of the collection"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"r"
argument_list|,
name|RUNZK
argument_list|,
literal|true
argument_list|,
literal|"run zk internally by passing the solr run port - only for clusters on one machine (tests, dev)"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"h"
argument_list|,
name|HELP
argument_list|,
literal|false
argument_list|,
literal|"bring up this help page"
argument_list|)
expr_stmt|;
try|try
block|{
comment|// parse the command line arguments
name|CommandLine
name|line
init|=
name|parser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
name|HELP
argument_list|)
operator|||
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|ZKHOST
argument_list|)
operator|||
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|CMD
argument_list|)
condition|)
block|{
comment|// automatically generate the help statement
name|HelpFormatter
name|formatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|formatter
operator|.
name|printHelp
argument_list|(
name|ZK_CLI_NAME
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Examples:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"zkcli.sh -zkhost localhost:9983 -cmd "
operator|+
name|BOOTSTRAP
operator|+
literal|" -"
operator|+
name|SOLRHOME
operator|+
literal|" /opt/solr"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"zkcli.sh -zkhost localhost:9983 -cmd "
operator|+
name|UPCONFIG
operator|+
literal|" -"
operator|+
name|CONFDIR
operator|+
literal|" /opt/solr/collection1/conf"
operator|+
literal|" -"
operator|+
name|CONFNAME
operator|+
literal|" myconf"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"zkcli.sh -zkhost localhost:9983 -cmd "
operator|+
name|DOWNCONFIG
operator|+
literal|" -"
operator|+
name|CONFDIR
operator|+
literal|" /opt/solr/collection1/conf"
operator|+
literal|" -"
operator|+
name|CONFNAME
operator|+
literal|" myconf"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"zkcli.sh -zkhost localhost:9983 -cmd "
operator|+
name|LINKCONFIG
operator|+
literal|" -"
operator|+
name|COLLECTION
operator|+
literal|" collection1"
operator|+
literal|" -"
operator|+
name|CONFNAME
operator|+
literal|" myconf"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"zkcli.sh -zkhost localhost:9983 -cmd "
operator|+
name|MAKEPATH
operator|+
literal|" /apache/solr"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"zkcli.sh -zkhost localhost:9983 -cmd "
operator|+
name|CLEAR
operator|+
literal|" /solr"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"zkcli.sh -zkhost localhost:9983 -cmd "
operator|+
name|LIST
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// start up a tmp zk server first
name|String
name|zkServerAddress
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|ZKHOST
argument_list|)
decl_stmt|;
name|String
name|solrHome
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|SOLRHOME
argument_list|)
decl_stmt|;
name|String
name|solrPort
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
name|RUNZK
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|SOLRHOME
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
operator|+
name|SOLRHOME
operator|+
literal|" is required for "
operator|+
name|RUNZK
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|solrPort
operator|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|RUNZK
argument_list|)
expr_stmt|;
block|}
name|SolrZkServer
name|zkServer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|solrPort
operator|!=
literal|null
condition|)
block|{
name|zkServer
operator|=
operator|new
name|SolrZkServer
argument_list|(
literal|"true"
argument_list|,
literal|null
argument_list|,
name|solrHome
operator|+
literal|"/zoo_data"
argument_list|,
name|solrHome
argument_list|,
name|solrPort
argument_list|)
expr_stmt|;
name|zkServer
operator|.
name|parseConfig
argument_list|()
expr_stmt|;
name|zkServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|SolrZkClient
name|zkClient
init|=
literal|null
decl_stmt|;
try|try
block|{
name|zkClient
operator|=
operator|new
name|SolrZkClient
argument_list|(
name|zkServerAddress
argument_list|,
literal|30000
argument_list|,
literal|30000
argument_list|,
operator|new
name|OnReconnect
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|command
parameter_list|()
block|{}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|getOptionValue
argument_list|(
name|CMD
argument_list|)
operator|.
name|equals
argument_list|(
name|BOOTSTRAP
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|SOLRHOME
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
operator|+
name|SOLRHOME
operator|+
literal|" is required for "
operator|+
name|BOOTSTRAP
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|SolrResourceLoader
name|loader
init|=
operator|new
name|SolrResourceLoader
argument_list|(
name|solrHome
argument_list|)
decl_stmt|;
name|solrHome
operator|=
name|loader
operator|.
name|getInstanceDir
argument_list|()
expr_stmt|;
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
name|SOLR_XML
argument_list|)
decl_stmt|;
name|boolean
name|isXml
init|=
literal|true
decl_stmt|;
if|if
condition|(
operator|!
name|configFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|configFile
operator|=
operator|new
name|File
argument_list|(
name|solrHome
argument_list|,
name|SolrProperties
operator|.
name|SOLR_PROPERTIES_FILE
argument_list|)
expr_stmt|;
name|isXml
operator|=
literal|false
expr_stmt|;
block|}
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
name|ConfigSolr
name|cfg
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isXml
condition|)
block|{
name|cfg
operator|=
operator|new
name|ConfigSolrXmlBackCompat
argument_list|(
name|loader
argument_list|,
literal|null
argument_list|,
name|is
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cfg
operator|=
operator|new
name|SolrProperties
argument_list|(
literal|null
argument_list|,
name|loader
argument_list|,
name|is
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|ZkController
operator|.
name|checkChrootPath
argument_list|(
name|zkServerAddress
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"A chroot was specified in zkHost but the znode doesn't exist. "
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|ZkController
operator|.
name|bootstrapConf
argument_list|(
name|zkClient
argument_list|,
name|cfg
argument_list|,
name|solrHome
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|getOptionValue
argument_list|(
name|CMD
argument_list|)
operator|.
name|equals
argument_list|(
name|UPCONFIG
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|CONFDIR
argument_list|)
operator|||
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|CONFNAME
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
operator|+
name|CONFDIR
operator|+
literal|" and -"
operator|+
name|CONFNAME
operator|+
literal|" are required for "
operator|+
name|UPCONFIG
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|confDir
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|CONFDIR
argument_list|)
decl_stmt|;
name|String
name|confName
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|CONFNAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ZkController
operator|.
name|checkChrootPath
argument_list|(
name|zkServerAddress
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"A chroot was specified in zkHost but the znode doesn't exist. "
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|ZkController
operator|.
name|uploadConfigDir
argument_list|(
name|zkClient
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|)
argument_list|,
name|confName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|getOptionValue
argument_list|(
name|CMD
argument_list|)
operator|.
name|equals
argument_list|(
name|DOWNCONFIG
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|CONFDIR
argument_list|)
operator|||
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|CONFNAME
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
operator|+
name|CONFDIR
operator|+
literal|" and -"
operator|+
name|CONFNAME
operator|+
literal|" are required for "
operator|+
name|DOWNCONFIG
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|confDir
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|CONFDIR
argument_list|)
decl_stmt|;
name|String
name|confName
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|CONFNAME
argument_list|)
decl_stmt|;
name|ZkController
operator|.
name|downloadConfigDir
argument_list|(
name|zkClient
argument_list|,
name|confName
argument_list|,
operator|new
name|File
argument_list|(
name|confDir
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|getOptionValue
argument_list|(
name|CMD
argument_list|)
operator|.
name|equals
argument_list|(
name|LINKCONFIG
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|COLLECTION
argument_list|)
operator|||
operator|!
name|line
operator|.
name|hasOption
argument_list|(
name|CONFNAME
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
operator|+
name|CONFDIR
operator|+
literal|" and -"
operator|+
name|CONFNAME
operator|+
literal|" are required for "
operator|+
name|LINKCONFIG
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|collection
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|COLLECTION
argument_list|)
decl_stmt|;
name|String
name|confName
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
name|CONFNAME
argument_list|)
decl_stmt|;
name|ZkController
operator|.
name|linkConfSet
argument_list|(
name|zkClient
argument_list|,
name|collection
argument_list|,
name|confName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|getOptionValue
argument_list|(
name|CMD
argument_list|)
operator|.
name|equals
argument_list|(
name|LIST
argument_list|)
condition|)
block|{
name|zkClient
operator|.
name|printLayoutToStdOut
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|getOptionValue
argument_list|(
name|CMD
argument_list|)
operator|.
name|equals
argument_list|(
name|CLEAR
argument_list|)
condition|)
block|{
name|List
name|arglist
init|=
name|line
operator|.
name|getArgList
argument_list|()
decl_stmt|;
if|if
condition|(
name|arglist
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
operator|+
name|CLEAR
operator|+
literal|" requires one arg - the path to clear"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|zkClient
operator|.
name|clean
argument_list|(
name|arglist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|line
operator|.
name|getOptionValue
argument_list|(
name|CMD
argument_list|)
operator|.
name|equals
argument_list|(
name|MAKEPATH
argument_list|)
condition|)
block|{
name|List
name|arglist
init|=
name|line
operator|.
name|getArgList
argument_list|()
decl_stmt|;
if|if
condition|(
name|arglist
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-"
operator|+
name|MAKEPATH
operator|+
literal|" requires one arg - the path to make"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|zkClient
operator|.
name|makePath
argument_list|(
name|arglist
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|solrPort
operator|!=
literal|null
condition|)
block|{
name|zkServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|zkClient
operator|!=
literal|null
condition|)
block|{
name|zkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|exp
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unexpected exception:"
operator|+
name|exp
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

