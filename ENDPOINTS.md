# 📡 Documentation des Endpoints - Al Baraka Digital Bank

## 👥 Jeux de données (utilisateurs de test)

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| **CLIENT** | client1@gmail.com | client1-pass |
| **AGENT** | agentbaraka@gmail.com | malika-pass |
| **AGENT** | malikaagent2@gmail.com | malika-pass |
| **ADMIN** | admin@gmail.com | admin-pass |

---

## 🧪 Tests Postman

**Collection** : `Al-barka-bank.postman_collection.json`

### 1️⃣ Authentification

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "client1@gmail.com",
  "password": "client1-pass"
}
```

**Réponse 200** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

> 💡 Copier le `token` dans `Authorization: Bearer <token>` pour les requêtes suivantes

---

### 2️⃣ Dépôt (DEPOSIT)

```http
POST http://localhost:8080/api/client/operations
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "type": "DEPOSIT",
  "amount": 1000.0
}
```

**Réponse 200** (montant ≤ 10 000 DH) :
```json
{
  "id": 1,
  "type": "DEPOSIT",
  "amount": 1000.0,
  "status": "VALIDATED",
  "createdAt": "2025-01-15T10:30:00"
}
```

> ⚠️ Le champ `type` est **obligatoire** et doit être l'une des valeurs : `DEPOSIT`, `WITHDRAWAL`, `TRANSFER`

> 💡 Le compte source est automatiquement identifié via le JWT

---

### 3️⃣ Retrait (WITHDRAWAL)

```http
POST http://localhost:8080/api/client/operations
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "type": "WITHDRAWAL",
  "amount": 500.0
}
```

**Réponse 200** :
```json
{
  "id": 2,
  "type": "WITHDRAWAL",
  "amount": 500.0,
  "status": "VALIDATED"
}
```

#### ❌ Cas d'erreur : Solde insuffisant

```http
POST http://localhost:8080/api/client/operations
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "type": "WITHDRAWAL",
  "amount": 999999.0
}
```

**Réponse 400** :
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Solde insuffisant pour effectuer cette opération"
}
```

---

### 4️⃣ Virement (TRANSFER)

```http
POST http://localhost:8080/api/client/operations
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "type": "TRANSFER",
  "amount": 750.0,
  "destinationAccountNumber": "6734518077"
}
```

**Réponse 200** :
```json
{
  "id": 3,
  "type": "TRANSFER",
  "amount": 750.0,
  "status": "VALIDATED",
  "destinationAccountNumber": "6734518077"
}
```

> 📝 Pour `TRANSFER`, `destinationAccountNumber` est **également obligatoire**

#### ❌ Cas d'erreur : Compte destinataire introuvable

```http
{
  "type": "TRANSFER",
  "amount": 500.0,
  "destinationAccountNumber": "9999999999"
}
```

**Réponse 404** :
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Compte destinataire introuvable"
}
```

#### ❌ Cas d'erreur : Même compte source/destination

**Réponse 400** :
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Le compte source et destination ne peuvent pas être identiques"
}
```

---

### 5️⃣ Lister mes opérations

```http
GET http://localhost:8080/api/client/operations?page=0&size=10
Authorization: Bearer <JWT_TOKEN>
```

**Réponse 200** :
```json
{
  "content": [
    {
      "id": 1,
      "type": "DEPOSIT",
      "amount": 1000.0,
      "status": "VALIDATED",
      "createdAt": "2025-01-15T10:30:00"
    },
    {
      "id": 2,
      "type": "TRANSFER",
      "amount": 15000.0,
      "status": "PENDING",
      "createdAt": "2025-01-15T11:00:00"
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

---

### 6️⃣ Upload justificatif (montant > 10 000 DH)

```http
POST http://localhost:8080/api/client/operations/2/document
Authorization: Bearer <JWT_TOKEN>
Content-Type: multipart/form-data

file: justificatif.pdf
```

**Réponse 200** :
```json
{
  "id": 1,
  "fileName": "justificatif.pdf",
  "fileType": "application/pdf",
  "uploadedAt": "2025-01-15T11:05:00"
}
```

> 📎 Formats acceptés : PDF, JPG, PNG (max 5MB)

---

### 7️⃣ Agent : Consulter opérations en attente (OAuth2)

#### a) Obtenir token OAuth2 de Keycloak

```http
POST http://localhost:8081/realms/albaraka-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=albaraka-backend
grant_type=password
username=agentbaraka@gmail.com
password=malika-pass
scope=openid email operations.read profile
```

**Réponse 200** :
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldU...",
  "expires_in": 300,
  "token_type": "Bearer",
  "scope": "openid email operations.read profile"
}
```

> 🔑 Le scope `operations.read` est **obligatoire** pour accéder aux opérations PENDING

#### b) Consulter les opérations PENDING

```http
GET http://localhost:8080/api/agent/operations/pending
Authorization: Bearer <OAUTH2_ACCESS_TOKEN>
```

**Réponse 200** :
```json
[
  {
    "id": 2,
    "type": "TRANSFER",
    "amount": 15000.0,
    "status": "PENDING",
    "accountSource": {
      "accountNumber": "6734518077",
      "balance": 25000.0
    },
    "createdAt": "2025-01-15T11:00:00"
  }
]
```

---

### 8️⃣ Agent : Approuver une opération

```http
PUT http://localhost:8080/api/agent/operations/2/approve
Authorization: Bearer <JWT_AGENT>
```

**Réponse 200** :
```json
{
  "id": 2,
  "status": "VALIDATED",
  "validatedAt": "2025-01-15T11:30:00"
}
```

---

### 9️⃣ Agent : Rejeter une opération

```http
PUT http://localhost:8080/api/agent/operations/2/reject
Authorization: Bearer <JWT_AGENT>
```

**Réponse 200** :
```json
{
  "id": 2,
  "status": "REJECTED",
  "validatedAt": "2025-01-15T11:35:00"
}
```

---

### 🔒 Tests de sécurité à vérifier

| Test | Endpoint | Résultat attendu |
|------|----------|------------------|
| Sans `Authorization` | `/api/client/operations` | **401 Unauthorized** |
| JWT CLIENT sur endpoint ADMIN | `/api/admin/**` | **403 Forbidden** |
| OAuth2 sans `operations.read` | `/api/agent/operations/pending` | **403 Forbidden** |
| OAuth2 avec `operations.read` | `/api/agent/operations/pending` | **200 OK** |
| JWT expiré | N'importe quel endpoint | **401 Unauthorized** |

#### Exemple : Sans Authorization

```http
GET http://localhost:8080/api/client/operations
# Sans header Authorization
```

**Réponse 401** :
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token d'authentification manquant ou invalide"
}
```

#### Exemple : Mauvais rôle

```http
GET http://localhost:8080/api/admin/users
Authorization: Bearer <JWT_CLIENT>
```

**Réponse 403** :
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Accès refusé : rôle insuffisant"
}
```

---

## 📡 Endpoints principaux (alignés avec la collection Postman)
### 🔐 Auth

| Méthode | Nom Postman       | Endpoint        | Description                  |
|---------|-------------------|-----------------|------------------------------|
| POST    | `Register`        | `/auth/register`| Inscription d’un utilisateur |
| POST    | `Login`           | `/auth/login`   | Authentification, retour du JWT interne |

---

### 👤 Client (`/api/client/**` – JWT rôle CLIENT)

| Méthode | Nom Postman      | Endpoint                     | Description                          |
|---------|------------------|------------------------------|--------------------------------------|
| GET     | Get My Account   | `/api/client/account/me`     | Infos du compte du client connecté  |

| Méthode | Nom Postman                      | Endpoint                               | Description                                                                 |
|---------|----------------------------------|----------------------------------------|-----------------------------------------------------------------------------|
| POST    | Create operation\<10k            | `/api/client/operations`              | Créer une opération (DEPOSIT/WITHDRAWAL/TRANSFER) – cas montant ≤ 10 000 DH |
| GET     | Mes operations                   | `/api/client/operations`              | Lister les opérations du client (pagination)                                |
| POST    | Create operation \> 10k (pending)| `/api/client/operations`              | Créer une opération avec montant > 10 000 DH → statut `PENDING`            |
| POST    | importer document                | `/api/client/operations/{id}/document`| Importer un justificatif (PDF/JPG/PNG) pour une opération PENDING           |

---

### 👨‍💼 Agent (`/api/agent/**`)

| Méthode | Nom Postman               | Endpoint                               | Authentification                               | Description                         |
|---------|---------------------------|----------------------------------------|------------------------------------------------|-------------------------------------|
| GET     | Lister opérations PENDING | `/api/agent/operations/pending`       | **OAuth2 Keycloak** + scope `operations.read`  | Lister les opérations avec statut `PENDING` (pagination) |
| PUT     | Valide operation          | `/api/agent/operations/{id}/approve`  | JWT interne, rôle `AGENT_BANCAIRE`            | Approuver une opération PENDING     |
| PUT     | Reject operation          | `/api/agent/operations/{id}/reject`   | JWT interne, rôle `AGENT_BANCAIRE`            | Rejeter une opération PENDING       |

| Méthode | Nom Postman                | Endpoint                                     | Authentification           | Description                               |
|---------|----------------------------|----------------------------------------------|----------------------------|-------------------------------------------|
| GET     | Trouver docs d'operation   | `/api/agent/operations/{id}/documents`      | JWT interne, rôle `AGENT_BANCAIRE` | Lister les justificatifs d’une opération  |
| GET     | Consulter document par id  | `/api/agent/documents/{documentId}/download`| JWT interne, rôle `AGENT_BANCAIRE` | Télécharger un justificatif par son id    |

---

### 👔 Admin (`/api/admin/**` – JWT rôle ADMIN)

| Méthode | Nom Postman              | Endpoint                         | Description                                      |
|---------|--------------------------|----------------------------------|--------------------------------------------------|
| POST    | Créer un User           | `/api/admin/users`              | Créer un utilisateur (CLIENT / AGENT / ADMIN)   |
| PUT     | active/desactive user   | `/api/admin/users/{id}/status`  | Changer le statut (actif / inactif) d’un user   |
| GET     | Lister les utilisateurs | `/api/admin/users`              | Lister les utilisateurs (pagination)            |
| GET     | Consulter un utilisateur par id | `/api/admin/users/{id}`    | Détails d’un utilisateur                        |
| DEL     | Désactiver un user      | `/api/admin/users/{id}`         | Désactiver un utilisateur (delete logique)      |


---

## 📊 Statuts des Opérations

| Statut | Description |
|--------|-------------|
| `PENDING` | En attente validation agent (montant > 10K) |
| `VALIDATED` | Approuvée et exécutée |
| `REJECTED` | Rejetée par l'agent |

---

<div align="center">

**Al Baraka Digital Bank API**

Développé par Salma Hamdi

</div>

