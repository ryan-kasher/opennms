/*global RequisitionNode:true */
/*jshint unused:false, sub:true */

/**
* @author Alejandro Galue <agalue@opennms.org>
* @copyright 2014 The OpenNMS Group, Inc.
*/

/**
* @ngdoc object
* @name QuickNode
* @module onms-requisitions
* @constructor
*/
function QuickNode() {

  'use strict';

  var self = this;

  self.foreignSource = null;
  self.foreignId = new Date().getTime() + '';
  self.nodeLabel = null;
  self.ipAddress = null;
  self.snmpCommunity = 'public';
  self.snmpVersion = 'v2c';
  self.noSnmp = false;
  self.deviceUsername = null;
  self.devicePassword = null;
  self.enablePassword = null;
  self.accessMethod = null;
  self.autoEnable = false;
  self.categories = [];

  /**
  * @description Creates a new RequisitionNode object based on the current settings.
  *
  * @name QuickNode:createRequisitionedNode
  * @ngdoc method
  * @methodOf QuickNode
  * @returns {object} the new RequisitionNode Object
  */
  self.createRequisitionedNode = function() {
    var reqNode = new RequisitionNode(self.foreignSource, {
      'foreign-id': self.foreignId,
      'node-label': self.nodeLabel,
      'interface': [{
        'ip-addr': self.ipAddress,
        'snmp-primary': self.noSnmp ? 'N' : 'P'
      }],
      'category': self.categories
    }, false);
    if (self.deviceUsername != null && self.deviceUsername != '') {
      reqNode.assets.push({'name': 'username', 'value': self.deviceUsername});
    }
    if (self.devicePassword != null && self.devicePassword != '') {
      reqNode.assets.push({'name': 'password', 'value': self.devicePassword});
    }
    if (self.enablePassword != null && self.enablePassword != '') {
      reqNode.assets.push({'name': 'enable', 'value': self.enablePassword});
    }
    if (self.accessMethod != null && self.accessMethod != '') {
      reqNode.assets.push({'name': 'connection', 'value': self.accessMethod});
    }
    if (self.autoEnable) {
      reqNode.assets.push({'name': 'autoenable', 'value': 'A'});
    }
    return reqNode;
  };

  /**
  * @description Adds a new category to the node
  *
  * @name QuickNode:addNewCategory
  * @ngdoc method
  * @methodOf QuickNode
  * @returns {object} the new service Object
  */
  self.addNewCategory = function() {
    self.categories.push({
      name: ''
    });
    return self.categories.length -1;
  };

  self.className = 'QuickNode';

  return self;
}
