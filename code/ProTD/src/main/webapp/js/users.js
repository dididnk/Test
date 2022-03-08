
var serverURI="http://localhost:8080/ProTD/quid?";

const app = new Vue({
	el: '#main',

	data: {
		users: [],
		loading: false
	},

	mounted(){
		this.loadUsers();
	},
	methods: {
		toDate(date) {
			if (isNaN(date))
				date = new Date(date);
			return date.toLocaleDateString();
		},
		loadUsers() {
			this.loading = true;
			fetch(serverURI, {
				"method": "POST",
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				body: 'Order=ListUser'
			}).then(response => {
				if (!response.ok)
					throw "Server returned " + response.status + " : " + response.statusText;
				return response.json();
			}).then(response => {
				for (user of response.data) {
					user.created = new Date(user.created);
				}
				this.users = response.data;
			}).catch(err => {
				console.log(err);
			}).finally(() => {
				this.loading = false;
			});
		},
		delUsers(idUser) {
			this.loading = true;
			fetch(serverURI, {
				"method": "POST",
				headers: {
					'Accept': 'application/json',
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				body: 'Order=DelUser&idUser=' + idUser
			}).then(response => {
				if (!response.ok)
					throw "Server returned " + response.status + " : " + response.statusText;
				return response.json()
			}).then(response => {
				if (response.code != 0)
					throw "Utilisateur introuvable !";
				let iUser = this.users.findIndex((a) => a.idUser == idUser);
				if (iUser >= 0)
					this.users.splice(iUser, 1);
			}).catch(err => {
				console.log(err);
			}).finally(() => {
				this.loading = false;
			});
		}
	}
})



