 
/*------------------------------------------------------------------------------------------------------
 		Создаём класс CustomValidator
 		Свойства :
 					isInValid:  			boolean значение где true - валидный объект, false - невалидный
 					invalidityMessages :    массив c сообщениями(String) об ошибках валидации 
 					input:					HTML-элемент откуда считываем значение (<input ...></input>)
 					output:					HTML-элемент куда выводим результат проверки валидатором
 					validityForChecks:		массив объектов с условиями для валидации
 		Методы:		
 					checkValidities():		проводит проверку значения input  на валидность, сохраняет состояние и ErrorMessages в объекте
 					toInject():				"актуализирует" информацию в output
 					addEventListener(callb)  добавляем элементу input обработку события onKeyup, внедряем callback
 					
-----------------------------------------------------------------------------------------------------*/
class CustomValidator{
   constructor(validityForChecks, input, output){
     this.isInValid = false;
     this.invalidityMessages = [];
     this.input = input;
     this.output = output;
     this.validityForChecks = validityForChecks;
   }
   checkValidities(){
	 this.invalidityMessages = [];
	 this.isInValid = true;
     for(var i = 0; i < this.validityForChecks.length; i++){
       if(!this.validityForChecks[i].checks(this.input)){
    	   this.isInValid = false;
    	   this.invalidityMessages.push(this.validityForChecks[i].inVMessage);
       }
     }
   }
   toInject(){
	  while (this.output.lastChild) {
		  this.output.removeChild(this.output.lastChild);
	  }
      if(!this.isInValid){
    	 
    	 var text = '<div>' + this.invalidityMessages.join('<br />') + '</div>';
    	 this.output.innerHTML = text;
     }
   }
   
   addEventListener(callback){
	   this.input.parentObject = this;
	   this.input.addEventListener('keyup', function() {
			this.parentObject.checkValidities();
			this.parentObject.toInject();
			callback();
		});
   }
      
 }

/*------------------------------------------------------------------
            Определяем массивы с условиями для валидации 
            значения поля формы регистрации.
            Условия представляют собою анонимные объекты
            и состоят из:
             1. checks: - функции, которая реализует проверку значения
             			переданого параметра и возвращает boolean значение:
             			true - если значение соответствует условию,
             			false -  если значение не соответствует условию валидации.
             2. inVmessage: - строка ошибки, которая возвращается в случае 
             				несоответсвия значения параметра условиям валидации.
        
-------------------------------------------------------------------*/
var userNameValidityChecks = [
  {
    checks : function(input){
        return !(input.value.length < 7);
    },
    inVMessage : 'Введите имя больше 6 знаков'    
  },
  {
    checks : function(input){
      return true;
    },
    inVMessage : 'Введённое имя пользователя уже существует в системе'
  },
  {
    checks : function(input){
      var illegalCharacters = input.value.match(/[^_a-zA-Z0-9]/g);
			return !illegalCharacters ;
    },
    inVMessage : 'Только цифры, буквы и знак _ должны присутствовать в Вашем '  + 'пользовательском имене'   
  }
];

var passwordValidityChecks = [
  {
    checks : function(input){
        return !(input.value.length < 8 | input.value.length > 38);
    },
    inVMessage : 'Введите  пароль больше 6 знаков'    
  },
  {
    checks : function(input){
      return input.value.match(/[0-9]/g);
    },
    inVMessage : 'Должна быть как минимум 1 цифра в пароле'
  },
  {
    checks : function(input){
     return input.value.match(/[a-zA-Z]/g);
    },
    inVMessage : 'Должна быть как минимум 1 буква в пароле'   
  }
];

var passwordRepeatValidityChecks = [
	{
		checks: function(input) {
			return input.value == document.querySelector('#password').value;
		},
		inVMessage: 'Введите корректно пароль'
	} 
];
var firstNameValidityChecks = [
  {
    checks : function(input){
        return !(input.value.length < 6);
    },
    inVMessage : 'Введите имя больше 6 знаков'    
  },
  {
    checks : function(input){
      var illegalCharacters = input.value.match(/[^a-zA-Z]/g);
			return !illegalCharacters;
    },
    inVMessage : 'Только буквы должны присутствовать в имени'   
  }
];
var lastNameValidityChecks = [
  {
    checks : function(input){
        return !(input.value.length < 6);
    },
    inVMessage : 'Введите имя больше 6 знаков'    
  },
  {
    checks : function(input){
      var illegalCharacters = input.value.match(/[^a-zA-Z]/g);
      return !illegalCharacters;
    },
    inVMessage : 'Только буквы должны присутствовать в фамилии'   
  }
];
var emailValidityChecks = [
  {
    checks : function(input){
    	return !(input.value < 6 | !input.value.match(/[^@]+@([^@\.]+\.)+[^@\.]+/g));
    },
    inVMessage : 'Введите корректный эмейл'   
  }
];
var phoneValidityChecks = [
  {
    checks : function(input){
      return !(input.value < 6 | !input.value.match(/^((8|\+7)[\- ]?)?(\(?\d{3}\)?[\- ]?)?[\d\- ]{7,10}$/g));
    },
    inVMessage : 'Введите корректный телефон'   
  }
];

/*------------------------------------------------------------------
				Создаём объекты - валидаторы
-------------------------------------------------------------------*/
var usernameValidator = new CustomValidator(userNameValidityChecks, document.getElementById('username'),
					document.querySelector('.username_err'));
var passwordValidator = new CustomValidator(passwordValidityChecks, document.getElementById('password'),
					document.querySelector('.password_err'));
var passwordConfirmValidator = new CustomValidator(passwordRepeatValidityChecks, document.getElementById('checkPass'),
					document.querySelector('.checkPass_err'));
var firstNameValidator = new CustomValidator(firstNameValidityChecks, document.getElementById('firstName'),
					document.querySelector('.firstName_err'));
var lastNameValidator = new CustomValidator(lastNameValidityChecks, document.getElementById('lastName'),
					document.querySelector('.lastName_err'));
var emailValidator = new CustomValidator(emailValidityChecks, document.getElementById('email'), document.querySelector('.email_err'));
var phoneValidator =  new CustomValidator(phoneValidityChecks, document.getElementById('phoneNumber'), document.querySelector('.phone_err'));

/*------------------------------------------------------------------
				Создаём массив валидаторов. 
				Создаём функцию toCheckValidationState() для обхода валидаторов и блокировки/разблокировки кнопки Submit формы регистрации ; 
				Передаём функцию как callback в валидаторы;
-------------------------------------------------------------------*/
var validatorsArray = [usernameValidator, passwordValidator, passwordConfirmValidator, firstNameValidator, lastNameValidator, emailValidator, phoneValidator];

var submitButton = document.querySelector("input[type=submit]");
var resetButton = document.querySelector("input[type=reset]");

var toCheckValidationState = function(){
	submitButton.disabled = true;
	var flag = true;
	for(var i = 0; i < validatorsArray.length; i++){
		if(!validatorsArray[i].isInValid){
			flag = false;
			break;
		}	
	}
	if(flag){
		submitButton.disabled=false;
	}
}

 for(var i = 0; i < validatorsArray.length; i++){
	 validatorsArray[i].addEventListener(toCheckValidationState);
 }
 
 resetButton.addEventListener('click', function(){
	 for(var i = 0; i < validatorsArray.length; i++){
			validatorsArray[i].isInValid = false;
			validatorsArray[i].invalidityMessages = [];
			validatorsArray[i].toInject();
		}
 });

 
