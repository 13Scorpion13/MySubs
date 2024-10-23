import requests
from aiogram import Bot, Dispatcher, types, F
from aiogram.filters import Command
from aiogram.types import Message, CallbackQuery, ReplyKeyboardMarkup, KeyboardButton, ReplyKeyboardRemove, InlineKeyboardMarkup, InlineKeyboardButton
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import StatesGroup, State
from aiogram.fsm.storage.memory import MemoryStorage
import asyncio

API_TOKEN = '7525196315:AAFS6AiaBdk9z8FSkltZET3uQ8otLxBXxZg'
JAVA_USER_API_URL = 'http://java-app:8080/api/users'
JAVA_SERVICE_API_URL = 'http://java-app:8080/api/services'
JAVA_CATEGORY_API_URL = 'http://java-app:8080/api/categories'
JAVA_TARIFF_API_URL = 'http://java-app:8080/api/tariffs'
JAVA_CARD_API_URL = 'http://java-app:8080/api/user-cards'
JAVA_SUBSCRIPTION_API_URL = 'http://java-app:8080/api/subscriptions'

bot = Bot(token=API_TOKEN)
storage = MemoryStorage()
dp = Dispatcher(storage=storage)

class RegistrationForm(StatesGroup):
    tg_id = State()
    nickname = State()
    email = State()

class CardForm(StatesGroup):
    card_name = State()
    last_num = State()

class SubscriptionForm(StatesGroup):
    service_id = State()
    category_id = State()
    tariff_id = State()
    user_card_id = State()

authorized_keyboard = ReplyKeyboardMarkup(
    keyboard=[[KeyboardButton(text="Добавить карту"), KeyboardButton(text="Добавить подписку"), KeyboardButton(text="Выйти")]],
    resize_keyboard=True
)

@dp.message(Command('start'))
async def cmd_start(message: Message, state: FSMContext):
    telegram_id = message.from_user.id
    response = requests.get(f"{JAVA_USER_API_URL}/by-telegram-id/{telegram_id}")

    if response.status_code == 200:
        await message.answer("Вы авторизованыю", reply_markup=authorized_keyboard)
    else:
        await message.answer("Вы не авторизованы. Пожалуйста, пройдите регистрацию с помощью команды /register.")    

@dp.message(Command('register'))
async def cmd_register(message: Message, state: FSMContext):
    tg_id = message.from_user.id
    await state.update_data(tg_id=tg_id)
    await state.set_state(RegistrationForm.nickname)
    await message.answer("Привет! Пожалуйста, введите ваш никнейм в Telegram.")

@dp.message(RegistrationForm.nickname)
async def process_nickname(message: Message, state: FSMContext):
    await state.update_data(nickname=message.text)
    await state.set_state(RegistrationForm.email)
    await message.answer("Теперь введите ваш email.")

@dp.message(RegistrationForm.email)
async def process_email(message: Message, state: FSMContext):
    await state.update_data(email=message.text)
    data = await state.get_data()

    user_data = {
        "telegramId": data['tg_id'],
        "nickname": data['nickname'],
        "email": data['email']
    }

    response = requests.post(JAVA_USER_API_URL, json=user_data)
    print("Отправляемые данные:", user_data)

    if response.status_code == 201:
        await message.answer("Данные успешно отправлены и сохранены!", reply_markup=authorized_keyboard)
    else:
        await message.answer("Произошла ошибка при отправке данных.")

    await state.clear()

@dp.message(F.text == "Добавить карту")
async def add_card(message: Message, state: FSMContext):
    telegram_id = message.from_user.id
    user_check_response = requests.get(f"{JAVA_USER_API_URL}/by-telegram-id/{telegram_id}")

    if user_check_response.status_code != 200:
        await message.answer("Вы не зарегистрированы. Пожалуйста, пройдите регистрацию с помощью команды /register.")
        return

    await state.set_state(CardForm.card_name)
    await message.answer("Пожалуйста, введите название карты.")

@dp.message(CardForm.card_name)
async def process_card_name(message: Message, state: FSMContext):
    await state.update_data(card_name=message.text)
    await state.set_state(CardForm.last_num)
    await message.answer("Введите последние 4 цифры карты или пропустите этот шаг, отправив команду /skip.")

@dp.message(CardForm.last_num)
async def process_last_num(message: Message, state: FSMContext):
    await state.update_data(last_num=message.text)
    data = await state.get_data()
    telegram_id = message.from_user.id

    card_data = {
        "cardName": data['card_name'],
        "lastNum": data['last_num']
    }

    response = requests.post(f"{JAVA_CARD_API_URL}/by-telegram-id?telegramId={telegram_id}", json=card_data)

    if response.status_code == 201:
        await message.answer("Карта успешно добавлена!", reply_markup=authorized_keyboard)
    else:
        await message.answer("Произошла ошибка при добавлении карты.")

    await state.clear()

@dp.message(Command('skip'))
async def skip_last_num(message: Message, state: FSMContext):
    await state.update_data(last_num=None)
    data = await state.get_data()
    telegram_id = message.from_user.id

    card_data = {
        "cardName": data['card_name'],
        "lastNum": None
    }

    response = requests.post(f"{JAVA_CARD_API_URL}/by-telegram-id?telegramId={telegram_id}", json=card_data)

    if response.status_code == 201:
        await message.answer("Карта успешно добавлена!", reply_markup=authorized_keyboard)
    else:
        await message.answer("Произошла ошибка при добавлении карты.")

    await state.clear()

@dp.message(F.text == "Добавить подписку")
async def add_subscription(message: Message, state: FSMContext):
    response = requests.get(f"{JAVA_SERVICE_API_URL}")
    if response.status_code == 200:
        services = response.json()
        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for service in services:
            keyboard.inline_keyboard.append([InlineKeyboardButton(text=service['serviceName'], callback_data=f'service_{service["id"]}')])
        await message.answer("Выберите сервис:", reply_markup=keyboard)
    else:
        await message.answer("Ошибка при загрузке сервисов.")

@dp.callback_query(lambda call: call.data.startswith('service_'))
async def process_service_selection(call: CallbackQuery, state: FSMContext):
    service_id = int(call.data.split('_')[1])
    await state.update_data(service_id=service_id)

    response = requests.get(f"{JAVA_CATEGORY_API_URL}")
    if response.status_code == 200:
        categories = response.json()
        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for category in categories:
            keyboard.inline_keyboard.append([InlineKeyboardButton(text=category['categoryName'], callback_data=f'category_{category["id"]}')])
        await call.message.answer("Выберите категорию:", reply_markup=keyboard)
    else:
        await call.message.answer("Ошибка при загрузке категорий.")

@dp.callback_query(lambda call: call.data.startswith('category_'))
async def process_category_selection(call: CallbackQuery, state: FSMContext):
    category_id = int(call.data.split('_')[1])
    await state.update_data(category_id=category_id)

    response = requests.get(f"{JAVA_TARIFF_API_URL}")
    if response.status_code == 200:
        tariffs = response.json()
        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for tariff in tariffs:
            keyboard.inline_keyboard.append([InlineKeyboardButton(text=f"{tariff['durationMonths']} мес - {tariff['price']} руб.", callback_data=f'tariff_{tariff["id"]}')])
        await call.message.answer("Выберите тариф:", reply_markup=keyboard)
    else:
        await call.message.answer("Ошибка при загрузке тарифов.")
    
@dp.callback_query(lambda call: call.data.startswith('tariff_'))
async def process_tariff_selection(call: CallbackQuery, state: FSMContext):
    tariff_id = int(call.data.split('_')[1])
    await state.update_data(tariff_id=tariff_id)

    telegram_id = call.from_user.id
    response = requests.get(f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")
    if response.status_code == 200:
        user_cards = response.json()
        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for user_card in user_cards:
            keyboard.inline_keyboard.append([InlineKeyboardButton(text=user_card['cardName'], callback_data=f'card_{user_card["id"]}')])
        await call.message.answer("Выберите карту:", reply_markup=keyboard)
    else:
        await call.message.answer("Ошибка при загрузке карт.")

@dp.callback_query(lambda call: call.data.startswith('card_'))
async def process_user_card_selection(call: CallbackQuery, state: FSMContext):
    user_card_id = int(call.data.split('_')[1])
    data = await state.get_data()
    telegram_id = call.from_user.id

    subscription_data = {
        'telegramId': telegram_id,
        'serviceId': data['service_id'],
        'categoryId': data['category_id'],
        'tariffId': data['tariff_id'],
        'userCardId': user_card_id
    }

    response = requests.post(f"{JAVA_SUBSCRIPTION_API_URL}/create", params=subscription_data)

    if response.status_code == 201:
        await call.message.answer("Подписка успешно добавлена!")
    else:
        await call.message.answer("Ошибка при добавлении подписки. Попробуйте снова.")

    await state.clear()

@dp.message(F.text == "Выйти")
async def cmd_logout(message: Message, state: FSMContext):
    await state.clear()
    await message.answer("Вы вышли из системы.", reply_markup=ReplyKeyboardRemove())

@dp.message(Command('cancel'))
async def cancel_handler(message: Message, state: FSMContext):
    await state.clear()
    await message.answer("Действие отменено.")

async def main():
    await dp.start_polling(bot)

if __name__ == '__main__':
    asyncio.run(main())
