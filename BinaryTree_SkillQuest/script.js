class TreeNode {
  constructor(key, title, meta = {}) {
    this.key = key;
    this.title = title;
    this.meta = meta;
    this.left = null;
    this.right = null;
  }
}

class BinarySearchTree {
  constructor() { this.root = null; }

  insert(key, title, meta = {}) {
    const newNode = new TreeNode(key, title, meta);
    if (!this.root) { this.root = newNode; return 'Inserted as root node'; }
    let current = this.root;
    while (true) {
      if (key === current.key) {
        current.title = title;
        current.meta = meta;
        return 'Updated existing node';
      }
      if (key < current.key) {
        if (!current.left) { current.left = newNode; return `Inserted ${key} to the LEFT of ${current.key}`; }
        current = current.left;
      } else {
        if (!current.right) { current.right = newNode; return `Inserted ${key} to the RIGHT of ${current.key}`; }
        current = current.right;
      }
    }
  }

  search(key) {
    const path = [];
    let current = this.root;
    while (current) {
      path.push(current.key);
      if (key === current.key) return { found: true, path, node: current };
      current = key < current.key ? current.left : current.right;
    }
    return { found: false, path, node: null };
  }

  traverse(type) {
    const result = [];
    const visit = node => { if (node) result.push(node.key); };
    const inorder = node => { if (!node) return; inorder(node.left); visit(node); inorder(node.right); };
    const preorder = node => { if (!node) return; visit(node); preorder(node.left); preorder(node.right); };
    const postorder = node => { if (!node) return; postorder(node.left); postorder(node.right); visit(node); };
    ({ inorder, preorder, postorder }[type])(this.root);
    return result;
  }

  count(node = this.root) { return node ? 1 + this.count(node.left) + this.count(node.right) : 0; }
  height(node = this.root) { return node ? 1 + Math.max(this.height(node.left), this.height(node.right)) : 0; }
}

const tree = new BinarySearchTree();
const defaults = [
  [50, 'Master Java Basics'], [30, 'Practice HTML UI'], [70, 'Build Database Plan'],
  [20, 'Review Variables'], [40, 'Design Login Page'], [65, 'Debug Backend'], [85, 'Present Final Output']
];
const canvas = document.getElementById('treeCanvas');
const output = document.getElementById('output');
const nodeCount = document.getElementById('nodeCount');
const heightCount = document.getElementById('heightCount');
const lastAction = document.getElementById('lastAction');
const activityDescription = document.getElementById('activityDescription');
let currentActivity = 'default';
let activeStructure = 'all';
const activities = {
  cards: {
    name: 'Playing Cards',
    description: 'Cards are inserted by card value. Lower cards move left, higher cards move right.',
    nodes: [
      [8, '8 of Hearts', { rank: '8', suit: '&hearts;', color: 'red' }],
      [4, '4 of Clubs', { rank: '4', suit: '&clubs;', color: 'black' }],
      [12, 'Queen of Diamonds', { rank: 'Q', suit: '&diams;', color: 'red' }],
      [2, '2 of Spades', { rank: '2', suit: '&spades;', color: 'black' }],
      [6, '6 of Diamonds', { rank: '6', suit: '&diams;', color: 'red' }],
      [10, '10 of Hearts', { rank: '10', suit: '&hearts;', color: 'red' }],
      [14, 'Ace of Spades', { rank: 'A', suit: '&spades;', color: 'black' }]
    ]
  },
  people: {
    name: 'Sort People by Height',
    description: 'People are arranged by height in centimeters, showing how a tree can organize a lineup.',
    nodes: [
      [170, 'Mika', { height: '170 cm', tone: 'violet' }],
      [158, 'Ana', { height: '158 cm', tone: 'teal' }],
      [184, 'Leo', { height: '184 cm', tone: 'gold' }],
      [151, 'Sam', { height: '151 cm', tone: 'rose' }],
      [164, 'Kai', { height: '164 cm', tone: 'blue' }],
      [176, 'Nia', { height: '176 cm', tone: 'green' }],
      [190, 'Ben', { height: '190 cm', tone: 'orange' }]
    ]
  },
  tasks: {
    name: 'Task Priority',
    description: 'Tasks are placed by priority score so urgent work can be searched and compared quickly.',
    nodes: [
      [50, 'Project', { level: 'Medium' }],
      [25, 'Review', { level: 'Low' }],
      [80, 'Submit', { level: 'High' }],
      [10, 'Warmup', { level: 'Low' }],
      [35, 'Practice', { level: 'Medium' }],
      [65, 'Debug', { level: 'High' }],
      [95, 'Present', { level: 'Critical' }]
    ]
  },
  prices: {
    name: 'Item Prices',
    description: 'Items are sorted by price, like organizing products from cheaper to more expensive.',
    nodes: [
      [45, 'Notebook', { price: '$45' }],
      [20, 'Pen Set', { price: '$20' }],
      [120, 'Keyboard', { price: '$120' }],
      [12, 'Eraser', { price: '$12' }],
      [35, 'Planner', { price: '$35' }],
      [90, 'Mouse', { price: '$90' }],
      [180, 'Monitor', { price: '$180' }]
    ]
  }
};

defaults.forEach(([key, title]) => tree.insert(key, title));
renderTree();

function loadActivity(activityKey) {
  const activity = activities[activityKey];
  currentActivity = activityKey;
  setActiveActivityButton();
  tree.root = null;
  activity.nodes.forEach(([key, title, meta]) => tree.insert(key, title, meta));
  activityDescription.textContent = activity.description;
  output.textContent = `${activity.name}: ${activity.nodes.map(([key, title]) => `${title} (${key})`).join(' -> ')}`;
  lastAction.textContent = activity.name;
  renderTree(activity.nodes.map(([key]) => key));
}

function insertFromInputs(actionLabel = 'Inserted') {
  const key = Number(document.getElementById('keyInput').value);
  const title = document.getElementById('titleInput').value.trim() || activityTitleFallback(key);
  const meta = buildActivityMeta(key, title);
  const message = tree.insert(key, title, meta);
  output.textContent = currentActivity === 'default'
    ? message
    : `${activities[currentActivity].name} activity: ${message}`;
  lastAction.textContent = actionLabel;
  renderTree([key]);
}

function activityTitleFallback(key) {
  if (currentActivity === 'cards') return `${cardRank(key)} Card`;
  if (currentActivity === 'people') return `Person ${key}`;
  if (currentActivity === 'tasks') return `Task ${key}`;
  if (currentActivity === 'prices') return `Item ${key}`;
  return 'Untitled Quest';
}

function buildActivityMeta(key, title) {
  if (currentActivity === 'cards') {
    const suits = ['&hearts;', '&clubs;', '&diams;', '&spades;'];
    const suit = suits[Math.abs(key) % suits.length];
    return {
      rank: cardRank(key),
      suit,
      color: suit === '&hearts;' || suit === '&diams;' ? 'red' : 'black'
    };
  }

  if (currentActivity === 'people') {
    const tones = ['violet', 'teal', 'gold', 'rose', 'blue', 'green', 'orange'];
    return { height: `${key} cm`, tone: tones[Math.abs(key) % tones.length] };
  }

  if (currentActivity === 'tasks') {
    const level = key >= 90 ? 'Critical' : key >= 65 ? 'High' : key >= 35 ? 'Medium' : 'Low';
    return { level };
  }

  if (currentActivity === 'prices') {
    return { price: `$${key}` };
  }

  return {};
}

function cardRank(key) {
  if (key === 14) return 'A';
  if (key === 13) return 'K';
  if (key === 12) return 'Q';
  if (key === 11) return 'J';
  return String(key);
}

function renderTree(highlights = []) {
  canvas.innerHTML = '';
  const positions = [];
  const activitySpacing = {
    cards: { levelGap: 120, minXGap: 122, startX: 112, startY: 76, minWidth: 900 },
    people: { levelGap: 118, minXGap: 136, startX: 120, startY: 76, minWidth: 980 },
    tasks: { levelGap: 108, minXGap: 116, startX: 108, startY: 70, minWidth: 860 },
    prices: { levelGap: 108, minXGap: 116, startX: 108, startY: 70, minWidth: 860 },
    default: { levelGap: 78, minXGap: 70, startX: 76, startY: 48, minWidth: 580 }
  };
  const spacing = activitySpacing[currentActivity] || activitySpacing.default;
  let order = 0;

  function assign(node, depth) {
    if (!node) return;
    assign(node.left, depth + 1);
    positions.push({
      node,
      x: spacing.startX + order * spacing.minXGap,
      y: spacing.startY + depth * spacing.levelGap,
      depth
    });
    order++;
    assign(node.right, depth + 1);
  }
  assign(tree.root, 0);

  if (!tree.root) {
    canvas.style.width = '100%';
    canvas.style.minWidth = '0';
    canvas.innerHTML = '<div class="empty-tree">Binary tree is empty. Insert a key to create a new root node.</div>';
    nodeCount.textContent = '0';
    heightCount.textContent = '0';
    return;
  }

  const maxX = Math.max(spacing.minWidth, spacing.startX * 2 + positions.length * spacing.minXGap);
  canvas.style.width = '100%';
  canvas.style.minWidth = '0';
  canvas.style.setProperty('--tree-scroll-width', `${maxX}px`);
  const scrollArea = document.createElement('div');
  scrollArea.className = 'tree-scroll-area';
  scrollArea.style.width = `${maxX}px`;
  scrollArea.style.height = `${Math.max(520, spacing.startY * 2 + tree.height() * spacing.levelGap)}px`;
  canvas.appendChild(scrollArea);

  function posOf(node) { return positions.find(p => p.node === node); }
  positions.forEach(({ node }) => {
    if (node.left) drawEdge(posOf(node), posOf(node.left));
    if (node.right) drawEdge(posOf(node), posOf(node.right));
  });
  positions.forEach(({ node, x, y, depth }) => {
    const div = document.createElement('div');
    const roles = structureRoles(node, depth);
    const isStructureActive = activeStructure !== 'all' && roles.includes(activeStructure);
    div.className = [
      'node',
      `${currentActivity}-node`,
      ...roles.map(role => `${role}-role`),
      isStructureActive ? 'structure-active' : '',
      isStructureActive ? `active-${activeStructure}` : '',
      activeStructure !== 'all' && !isStructureActive ? 'structure-muted' : '',
      highlights.includes(node.key) ? 'highlight' : ''
    ].join(' ');
    div.style.left = `${x}px`; div.style.top = `${y}px`;
    div.innerHTML = nodeTemplate(node);
    canvas.appendChild(div);
  });
  nodeCount.textContent = tree.count();
  heightCount.textContent = tree.height();
}

function structureRoles(node, depth) {
  const roles = [];
  if (depth === 0) roles.push('root');
  if (node.left || node.right) roles.push('parent');
  if (depth > 0) roles.push('child');
  return roles;
}

function nodeTemplate(node) {
  if (currentActivity === 'cards') {
    const rank = node.meta.rank || String(node.key);
    const suit = node.meta.suit || '&bull;';
    const colorClass = node.meta.color === 'red' ? 'red-card' : 'black-card';
    return `
      <div class="playing-card ${colorClass}">
        <span class="card-corner top">${rank}<small>${suit}</small></span>
        <strong>${suit}</strong>
        <span class="card-label">${node.title}</span>
        <span class="card-corner bottom">${rank}<small>${suit}</small></span>
      </div>
    `;
  }

  if (currentActivity === 'people') {
    const height = node.meta.height || `${node.key} cm`;
    const tone = node.meta.tone || 'violet';
    return `
      <div class="person-visual ${tone}">
        <div class="person-ruler"><span style="height:${Math.min(92, Math.max(42, node.key / 2))}%"></span></div>
        <div class="person-body">
          <span class="person-head">${node.title.charAt(0)}</span>
          <span class="person-torso"></span>
        </div>
        <b>${node.title}</b>
        <small>${height}</small>
      </div>
    `;
  }

  if (currentActivity === 'tasks') {
    const level = node.meta.level || 'Custom';
    return `
      <div class="task-visual">
        <span class="task-pin"></span>
        <span>${level}</span>
        <b>${node.key}</b>
        <small>${node.title}</small>
      </div>
    `;
  }

  if (currentActivity === 'prices') {
    const price = node.meta.price || `$${node.key}`;
    return `
      <div class="price-visual">
        <span class="tag-hole"></span>
        <span>${price}</span>
        <b>${node.title}</b>
        <small>Key ${node.key}</small>
      </div>
    `;
  }

  return `<b>${node.key}</b><small>${node.title}</small>`;
}

function drawEdge(a, b) {
  const dx = b.x - a.x, dy = b.y - a.y;
  const length = Math.sqrt(dx * dx + dy * dy);
  const edge = document.createElement('div');
  edge.className = 'edge';
  edge.style.width = `${length}px`;
  edge.style.left = `${a.x}px`;
  edge.style.top = `${a.y}px`;
  edge.style.transform = `translateY(-50%) rotate(${Math.atan2(dy, dx)}rad)`;
  canvas.appendChild(edge);
}

document.getElementById('insertBtn').addEventListener('click', () => {
  insertFromInputs('Inserted');
});

document.querySelectorAll('[data-traverse]').forEach(btn => btn.addEventListener('click', () => {
  const type = btn.dataset.traverse;
  const result = tree.traverse(type);
  output.textContent = result.length
    ? `${type.toUpperCase()} traversal: ${result.join(' -> ')}`
    : 'Tree is empty. Insert a root node first.';
  lastAction.textContent = type;
  animate(result);
}));

document.getElementById('searchBtn').addEventListener('click', () => {
  const key = Number(document.getElementById('searchInput').value);
  const result = tree.search(key);
  output.textContent = result.found
    ? `FOUND ${key}. Search path: ${result.path.join(' -> ')}`
    : `NOT FOUND. Search path ended at: ${result.path.join(' -> ') || 'empty tree'}`;
  lastAction.textContent = result.found ? 'Found' : 'Missing';
  renderTree(result.path);
});

document.getElementById('resetBtn').addEventListener('click', () => {
  activeStructure = 'all';
  setActiveStructureButton();
  if (currentActivity !== 'default') {
    loadActivity(currentActivity);
    output.textContent = `${activities[currentActivity].name} activity reset. You can insert new data into this activity.`;
    lastAction.textContent = 'Activity Reset';
    return;
  }

  tree.root = null;
  output.textContent = 'Tree cleared. Insert a new root to start again.';
  activityDescription.textContent = 'Click an activity to see how binary trees organize real-life data.';
  lastAction.textContent = 'Reset';
  renderTree();
});

document.querySelectorAll('[data-activity]').forEach(btn => btn.addEventListener('click', () => {
  loadActivity(btn.dataset.activity);
}));

document.querySelectorAll('[data-structure]').forEach(btn => btn.addEventListener('click', () => {
  activeStructure = btn.dataset.structure;
  setActiveStructureButton();
  output.textContent = activeStructure === 'all'
    ? 'Showing all binary tree structure colors.'
    : `Highlighting ${activeStructure.toUpperCase()} nodes in the binary tree.`;
  lastAction.textContent = activeStructure === 'all' ? 'All' : activeStructure;
  renderTree();
}));

function setActiveStructureButton() {
  document.querySelectorAll('[data-structure]').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.structure === activeStructure);
  });
}

function setActiveActivityButton() {
  document.querySelectorAll('[data-activity]').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.activity === currentActivity);
  });
}

function animate(sequence) {
  if (!sequence.length) {
    renderTree();
    return;
  }

  let index = 0;
  const timer = setInterval(() => {
    renderTree(sequence.slice(0, index + 1));
    index++;
    if (index >= sequence.length) clearInterval(timer);
  }, 420);
}
