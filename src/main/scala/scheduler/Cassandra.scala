package scheduler

import com.pagerduty.eris.ClusterCtx
import com.netflix.astyanax.connectionpool.impl.{ConnectionPoolConfigurationImpl, Slf4jConnectionPoolMonitorImpl}
import com.netflix.astyanax.connectionpool.{ConnectionPoolConfiguration, ConnectionPoolMonitor, NodeDiscoveryType}
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl
import com.netflix.astyanax.thrift.ThriftFamilyFactory
import com.netflix.astyanax.{AstyanaxConfiguration, AstyanaxContext, Cluster, Keyspace}
import com.typesafe.config.Config


object Cassandra {
  def get: ClusterCtx = {
    val config = new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.RING_DESCRIBE)
    val poolConfig = (new ConnectionPoolConfigurationImpl("MyConnectionPool").
                        setPort(9160).
                        setMaxConnsPerHost(1).
                        setSeeds("127.0.0.1:9160"))
    val poolMonitor = new Slf4jConnectionPoolMonitorImpl()
    new ClusterCtx(
       clusterName="Test Cluster",
       astyanaxConfig=config,
       connectionPoolConfig=poolConfig,
       connectionPoolMonitor=poolMonitor
    )
  }
}
