import requests
import time
import multiprocessing
from notifications import async_main
from dateutil.parser import parse
from datetime import datetime
from aiogram import Bot, Dispatcher, types, F
from aiogram.filters.state import StateFilter
from aiogram.filters import Command, CommandStart
from aiogram.types import Message, CallbackQuery, ReplyKeyboardMarkup, KeyboardButton, ReplyKeyboardRemove, InlineKeyboardMarkup, InlineKeyboardButton, WebAppInfo
from aiogram.fsm.context import FSMContext
from aiogram.fsm.state import StatesGroup, State
from aiogram.fsm.storage.memory import MemoryStorage
from dotenv import load_dotenv
from text import Hello, after_reg_text, subs_text, add_sub, add_service, sub_duration, add_service_custom, add_category, user_sub, profile_info, auth_text, donations_text, select_edit, interaction_with_cards, my_cards_text, select_edit_subs, select_edit_custom_subs, analicitcs_text, agreement_second_time, agreement_first_time, help_text, guide_text_sber, guide_text_yandex, feedback_text, guide_text_ivi


import asyncio
import re
import fitz
import os
import dateparser

load_dotenv()

API_TOKEN = os.getenv('API_TOKEN')
JAVA_USER_API_URL = os.getenv('JAVA_USER_API_URL')
JAVA_SERVICE_API_URL = os.getenv('JAVA_SERVICE_API_URL')
JAVA_CATEGORY_API_URL = os.getenv('JAVA_CATEGORY_API_URL')
JAVA_TARIFF_API_URL = os.getenv('JAVA_TARIFF_API_URL')
JAVA_CARD_API_URL = os.getenv('JAVA_CARD_API_URL')
JAVA_SUBSCRIPTION_API_URL = os.getenv('JAVA_SUBSCRIPTION_API_URL')
JAVA_CUSTOM_SUBSCRIPTION_API_URL = os.getenv('JAVA_CUSTOM_SUBSCRIPTION_API_URL')

bot = Bot(token=API_TOKEN)
storage = MemoryStorage()
dp = Dispatcher(storage=storage)


class RegistrationForm(StatesGroup):
    tg_id = State()
    nickname = State()


class CardForm(StatesGroup):
    card_name = State()
    last_num = State()


class SubscriptionForm(StatesGroup):
    category_id = State()
    service_id = State()
    tariff_id = State()
    user_card_id = State()
    start_date = State()


class CustomSubscriptionForm(StatesGroup):
    service_name = State()
    duration_months = State()
    price = State()
    user_card_id = State()
    date = State()

class CustomSubscriptionChekForm(StatesGroup):
    service_name = State()
    duration_months = State()
    price = State()
    user_card_id = State()
    date = State()
    waiting_for_card_selection = State()
    waiting_for_consent_change = State()


class EditSubscriptionForm(StatesGroup):
    tariff_id = State()
    user_card_id = State()


authorized_keyboard = ReplyKeyboardMarkup(
    keyboard=[
        [KeyboardButton(text="Личный кабинет"),
         KeyboardButton(text="Подписки")]
    ],
    resize_keyboard=True
)

profile = InlineKeyboardMarkup(inline_keyboard=[
    [InlineKeyboardButton(text='Редактировать',
                          callback_data='edit_user_info')],
    [InlineKeyboardButton(text='Карты', callback_data='card_profile')],
    [InlineKeyboardButton(text='Донаты', callback_data='donations')]
])

card_action = InlineKeyboardMarkup(inline_keyboard=[
    [InlineKeyboardButton(text='Мои карты', callback_data='my_cards')],
    [InlineKeyboardButton(text='Добавить карту', callback_data='add_card')],
    [InlineKeyboardButton(text='Назад', callback_data='back_to_profile')]
])

subs = InlineKeyboardMarkup(inline_keyboard=[
    [InlineKeyboardButton(text='Мои подписки', callback_data='my_subs')],
    [InlineKeyboardButton(text='Добавить подписку', callback_data='add_subs'),
     InlineKeyboardButton(text='Аналитика', callback_data='analitics')]
])

add_subs_scan_post = InlineKeyboardMarkup(inline_keyboard=[
    [InlineKeyboardButton(text='Добавить собственную', callback_data='add_custom'),
     InlineKeyboardButton(text='Добавить предложенную', callback_data='add_from_list')],
    [InlineKeyboardButton(text='Просканировать чек',
                          callback_data='scan_chek')],
    [InlineKeyboardButton(text='Назад', callback_data='back_to_subs')]
])

@dp.message(Command('help'))
async def send_help(message: Message):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [
            InlineKeyboardButton(text="Отменить/подключить подписку", callback_data="subscription_guide"),
            InlineKeyboardButton(text="Обратная связь", callback_data="feedback")
        ]
    ])
    await message.answer(help_text, reply_markup=keyboard)

@dp.callback_query(F.data == "subscription_guide")
async def show_subscription_options(callback: CallbackQuery):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [
            InlineKeyboardButton(text="СберПрайм", callback_data="guide_sber"),
            InlineKeyboardButton(text="Яндекс Плюс", callback_data="guide_yandex"),
            InlineKeyboardButton(text="IvI", callback_data="guide_ivi")
        ],
        [
            InlineKeyboardButton(text="Назад", callback_data="help")
        ]
    ])
    await callback.message.edit_text("Выберите подписку для получения инструкции:", reply_markup=keyboard)

@dp.callback_query(F.data == "guide_sber")
async def show_sber_guide(callback: CallbackQuery):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Назад", callback_data="help")]
    ])
    await callback.message.edit_text(guide_text_sber, reply_markup=keyboard)

@dp.callback_query(F.data == "guide_yandex")
async def show_yandex_guide(callback: CallbackQuery):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Назад", callback_data="help")]
    ])
    await callback.message.edit_text(guide_text_yandex, reply_markup=keyboard)

@dp.callback_query(F.data == "guide_ivi")
async def show_ivi_guide(callback: CallbackQuery):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Назад", callback_data="help")]
    ])
    await callback.message.edit_text(guide_text_ivi, reply_markup=keyboard)

@dp.callback_query(F.data == "feedback")
async def show_feedback(callback: CallbackQuery):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Назад", callback_data="help")]
    ])
    await callback.message.edit_text(feedback_text, reply_markup=keyboard)

@dp.callback_query(F.data == "help")
async def return_to_help(callback: CallbackQuery):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [
            InlineKeyboardButton(text="Отменить/подключить подписку", callback_data="subscription_guide"),
            InlineKeyboardButton(text="Обратная связь", callback_data="feedback")
        ]
    ])
    await callback.message.edit_text(help_text, reply_markup=keyboard)



@dp.callback_query(F.data == 'donations')
async def show_donations(call: CallbackQuery, state: FSMContext):
    keyboard = InlineKeyboardMarkup(inline_keyboard=[])
    keyboard.inline_keyboard.append(
        [InlineKeyboardButton(
            text="Назад", callback_data='back_to_profile')])
    await call.message.edit_text(donations_text, reply_markup=keyboard, parse_mode='Markdown')


@dp.callback_query(F.data == 'analitics')
async def show_analytics(call: CallbackQuery, state: FSMContext):
    telegram_id = call.from_user.id

    subs_response = requests.get(
        f"{JAVA_SUBSCRIPTION_API_URL}/analytics/{telegram_id}")
    if subs_response.status_code == 200:
        subs_data = subs_response.json()
        subs_total = subs_data['totalAmount']
        subscriptions = subs_data['subscriptions']
    else:
        subs_total = 0
        subscriptions = []

    custom_response = requests.get(
        f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/analytics/{telegram_id}")
    if custom_response.status_code == 200:
        custom_data = custom_response.json()
        custom_total = custom_data['totalAmount']
        custom_subscriptions = custom_data['customSubscriptions']
    else:
        custom_total = 0
        custom_subscriptions = []

    total_expense = subs_total + custom_total

    text = f"💰 *Списания в этом месяце:* {total_expense} руб.\n"
    text += analicitcs_text
    text += "🔽 Подписки, которые спишутся:\n"

    keyboard = InlineKeyboardMarkup(inline_keyboard=[])
    for sub in subscriptions:
        service_name = sub['serviceName']
        price = sub['tariffPrice']
        duration = sub['tariffDuration']
        button_text = f"{service_name}: {duration} мес - {price} руб."
        keyboard.inline_keyboard.append([InlineKeyboardButton(
            text=button_text, callback_data=f"subscription_{sub['id']}")])

    for custom_sub in custom_subscriptions:
        service_name = custom_sub['serviceName']
        price = custom_sub['price']
        duration = custom_sub['durationMonths']
        button_text = f"{service_name}: {duration} мес - {price} руб."
        keyboard.inline_keyboard.append([InlineKeyboardButton(
            text=button_text, callback_data=f"custom_subscription_{custom_sub['id']}")])

    keyboard.inline_keyboard.append(
        [InlineKeyboardButton(
            text="Назад", callback_data='back_to_subs')]
    )

    await call.message.edit_text(text, reply_markup=keyboard, parse_mode="Markdown")


@dp.callback_query(F.data == 'back_to_subs')
async def back_to_subs(callback: CallbackQuery):
    await callback.message.edit_text(subs_text, reply_markup=subs)


@dp.callback_query(F.data == 'back_to_profile')
async def back_to_profile(callback: CallbackQuery):
    nickname = await get_user_info(callback.from_user.id)
    await callback.message.edit_text(text=f'''
🌟 Добро пожаловать, {nickname}!   
{profile_info}
    ''', reply_markup=profile)


async def get_user_info(id):
    telegram_id = id
    response = requests.get(
        f"{JAVA_USER_API_URL}/by-telegram-id/{telegram_id}")
    user_profile = response.json()
    nickname = user_profile["nickname"]
    return nickname

def extract_text_from_pdf(file_path):
    text = ""
    try:
        with fitz.open(file_path) as pdf:
            for page in pdf:
                text += page.get_text()
    except Exception as e:
        return f"Ошибка при обработке PDF: {e}"
    return text

def extract_sberbank(text):
    pattern_sberbank = (
        r"Описание\s+"
        r"([\w\.\-\+ ]+)"
        r".*?"
        r"Операция совершена\s+"
        r"(\d{1,2}\s[а-яА-Я]+\s\d{4})"
        r".*?"
        r"Сумма\sв\sвалюте\sоперации[^\d]+(\d{1,3}(?:[.,]\d{2})?)\s?₽"
    )
    matches = re.findall(pattern_sberbank, text, re.IGNORECASE | re.DOTALL)
    subscriptions = []
    for match in matches:
        name, date, price = match
        subscriptions.append({
            "Название подписки": name.strip(),
            "Дата": date,
            "Стоимость": f"{price}"
        })
    return subscriptions

@dp.callback_query(F.data == 'scan_chek')
async def check_consent_and_request_pdf(call: CallbackQuery, state: FSMContext):
    telegram_id = call.from_user.id
    response = requests.get(f"{JAVA_USER_API_URL}/by-telegram-id/{telegram_id}")
    if response.status_code == 200:
        user_data = response.json()
        if user_data['consent']:
            await call.message.edit_text("Отправьте PDF чек для извлечения данных о подписке.")
        else:
            keyboard = InlineKeyboardMarkup(inline_keyboard=[
                [InlineKeyboardButton(text="Согласен", callback_data="consent_yes")],
                [InlineKeyboardButton(text="Не согласен", callback_data="consent_no")]
            ])
            await call.message.answer(agreement_second_time, reply_markup=keyboard)
            await state.set_state(CustomSubscriptionChekForm.waiting_for_consent_change)
    else:
        await message.reply("Произошла ошибка при проверке данных пользователя. Попробуйте позже.")

@dp.callback_query(F.data == "consent_yes")
async def process_consent_yes(call: CallbackQuery, state: FSMContext):
    telegram_id = call.from_user.id
    response = requests.patch(
        f"{JAVA_USER_API_URL}/{telegram_id}",
        json={"consent": True}
    )
    if response.status_code == 200:
        await call.message.edit_text("Согласие сохранено. Отправьте PDF чек для обработки.")
        await state.clear()
    else:
        await call.message.edit_text("Произошла ошибка при сохранении согласия. Попробуйте позже.")
        await state.clear()

@dp.callback_query(F.data == "consent_no")
async def process_consent_no(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text("Вы отказались от обработки данных. Эта функция недоступна.")
    await state.clear()

@dp.message(F.document)
async def handle_document(message: Message, state: FSMContext):
    telegram_id = message.from_user.id
    document = message.document

    if document.mime_type != "application/pdf":
        await message.reply("Пожалуйста, отправьте PDF файл.")
        return

    file_path = f"{document.file_id}.pdf"
    file = await bot.get_file(document.file_id)
    await bot.download_file(file.file_path, file_path)

    extracted_text = extract_text_from_pdf(file_path)
    os.remove(file_path)

    subscriptions = extract_sberbank(extracted_text)

    if subscriptions:
        await state.update_data(subscriptions=subscriptions)
        response = requests.get(f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")
        if response.status_code == 200:
            user_cards = response.json()
            if not user_cards:
                await message.reply("У вас нет добавленных карт. Добавьте карту перед созданием подписки.")
                return
            keyboard = InlineKeyboardMarkup(inline_keyboard=[])
            for card in user_cards:
                if card['lastNum'] != None:
                    button_text = f"{card['cardName']} ···· {card['lastNum']}"
                else:
                    button_text = f"{card['cardName']}"
                keyboard.inline_keyboard.append(
                    [InlineKeyboardButton(text=button_text, callback_data=f"card_chek_{card['id']}")])
            await message.reply("Выберите карту для подписок:", reply_markup=keyboard)
            await state.set_state(CustomSubscriptionChekForm.waiting_for_card_selection)
        else:
            await message.reply("Ошибка при загрузке ваших карт.")
    else:
        await message.reply("Не удалось найти информацию о подписках в чеках Сбербанка.")

@dp.callback_query(lambda call: call.data.startswith('card_chek_'))
async def process_card_selection(call: CallbackQuery, state: FSMContext):
    card_id = int(call.data.split('_')[2])
    data = await state.get_data()
    subscriptions = data.get('subscriptions', [])
    successful_additions = 0
    errors = []

    for sub in subscriptions:
        parsed_date = dateparser.parse(sub["Дата"])
        formatted_date = parsed_date.strftime("%Y-%m-%d")
                
        price = float(sub["Стоимость"].replace(",", "."))

        subscription_data = {
            "userId": call.from_user.id,
            "serviceName": sub["Название подписки"],
            "durationMonths": 1,
            "price": price,
            "userCardId": card_id,
            "date": formatted_date
        }

        response = requests.post(
            f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/create-custom-subs", json=subscription_data)
        if response.status_code == 201:
            successful_additions += 1
        else:
            errors.append(f"Ошибка при добавлении подписки: {response.text}")
    
    if successful_additions > 0:
        await call.message.edit_text(f"Подписка успешно добавлена!.")
        await asyncio.sleep(2)
        await call.message.edit_text(subs_text, reply_markup=subs)
    if errors:
        await call.message.answer("\n".join(errors))

    await state.clear()


@dp.message(F.text == "Личный кабинет")
async def shure_profile(message: Message):
    nickname = await get_user_info(message.from_user.id)
    await message.answer(text=f'''
🌟 Добро пожаловать, {nickname}!
{profile_info}
    ''', reply_markup=profile)


@dp.callback_query(F.data == 'card_profile')
async def shure_card_profile(callback: CallbackQuery):
    await callback.message.edit_text(interaction_with_cards, reply_markup=card_action)


@dp.callback_query(F.data == 'edit_user_info')
async def process_edit_user_info(call: CallbackQuery, state: FSMContext):
    telegram_id = call.from_user.id
    await state.update_data(telegram_id=telegram_id)
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(
            text="Редактировать никнейм", callback_data=f"action_edit_nickname_{telegram_id}")],
        [InlineKeyboardButton(
            text="Назад", callback_data="back_to_profile")]
    ])
    await call.message.edit_text(select_edit, reply_markup=keyboard)


@dp.callback_query(lambda call: call.data.startswith('action_edit_nickname'))
async def edit_nickname(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text("Введите новый никнейм!")
    await state.set_state("edit_nickname")


@dp.message(StateFilter("edit_nickname"))
async def process_edit_nickname(message: Message, state: FSMContext):
    new_nickname = message.text
    data = await state.get_data()
    telegram_id = data['telegram_id']

    update_data = {"nickname": new_nickname}
    response = requests.patch(
        f"{JAVA_USER_API_URL}/{telegram_id}", json=update_data)

    nickname = await get_user_info(message.from_user.id)

    if response.status_code == 200:
        msg = await message.answer("Никнейм успешно обновлен!")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id,
                                    text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)
    else:
        msg = await message.answer("Ошибка при обновлении никнейма!")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id,
                                    text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)

    await state.clear()

@dp.message(Command('start'))
async def cmd_start(message: Message, state: FSMContext):
    telegram_id = message.from_user.id
    response = requests.get(
        f"{JAVA_USER_API_URL}/by-telegram-id/{telegram_id}")

    if response.status_code == 200:
        await message.answer(auth_text, reply_markup=authorized_keyboard)
    else:
        await message.answer("Вы не авторизованы. Пожалуйста, пройдите регистрацию с помощью команды /register.")


@dp.message(Command('register'))
async def cmd_register(message: Message, state: FSMContext):
    tg_id = message.from_user.id
    response = requests.get(
        f"{JAVA_USER_API_URL}/by-telegram-id/{tg_id}")
    if response.status_code == 200:
        await message.answer("Вы уже зарегистрированы! Если возникли проблемы, попробуйте заново написать команду /start или обратиться в поддержку по команде /help", reply_markup=authorized_keyboard)
    else:
        await state.update_data(tg_id=tg_id)
        await state.set_state(RegistrationForm.nickname)
        await message.answer("Пожалуйста, представьтесь!")


@dp.message(RegistrationForm.nickname)
async def process_nickname(message: Message, state: FSMContext):
    await state.update_data(nickname=message.text)
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Согласен", callback_data="consent_true")],
        [InlineKeyboardButton(text="Не согласен", callback_data="consent_false")]
    ])
    await message.answer(agreement_first_time, reply_markup=keyboard)


@dp.callback_query(lambda call: call.data.startswith("consent_"))
async def process_consent_selection(call: CallbackQuery, state: FSMContext):
    consent = call.data.split('_')[1]
    await state.update_data(consent=consent)

    data = await state.get_data()
    user_data = {
        "telegramId": data['tg_id'],
        "nickname": data['nickname'],
        "consent": consent
    }

    response = requests.post(f"{JAVA_USER_API_URL}/create", json=user_data)
    print("Отправляемые данные:", user_data)

    if response.status_code == 201:
        await call.message.delete()
        await call.message.answer(after_reg_text, reply_markup=authorized_keyboard)
    else:
        await call.message.edit_text("Произошла ошибка при отправке данных.")

    await state.clear()

@dp.callback_query(F.data == "add_card")
async def add_card(callback: CallbackQuery, state: FSMContext):
    telegram_id = callback.from_user.id
    user_check_response = requests.get(
        f"{JAVA_USER_API_URL}/by-telegram-id/{telegram_id}")

    if user_check_response.status_code != 200:
        await callback.message.answer("Вы не зарегистрированы. Пожалуйста, пройдите регистрацию с помощью команды /register.")
        return

    await state.set_state(CardForm.card_name)
    await callback.message.edit_text("Пожалуйста, введите название карты.")

@dp.message(CardForm.card_name)
async def process_card_name(message: Message, state: FSMContext):
    await state.update_data(card_name=message.text)
    await state.set_state(CardForm.last_num)
    await message.answer("Введите последние 4 цифры карты или пропустите этот шаг, отправив команду /skip.")
    await bot.delete_message(message.chat.id, message.message_id)
    await bot.delete_message(message.chat.id, message.message_id - 1)

@dp.message(Command('skip'), CardForm.last_num)
async def skip_last_num(message: Message, state: FSMContext):
    await state.update_data(last_num=None)
    data = await state.get_data()
    telegram_id = message.from_user.id
    nickname = await get_user_info(message.from_user.id)

    card_data = {
        "cardName": data['card_name'],
        "lastNum": None
    }

    response = requests.post(
        f"{JAVA_CARD_API_URL}/by-telegram-id?telegramId={telegram_id}", json=card_data)

    if response.status_code == 201:
        msg = await message.answer("Карта успешно добавлена!")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id,
                                    text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)
    else:
        await message.answer("Произошла ошибка при добавлении карты.")
        await message.answer(text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)

    await state.clear()

@dp.message(CardForm.last_num)
async def process_last_num(message: Message, state: FSMContext):
    last_num = message.text.strip()
    if not re.fullmatch(r"\d{4}", last_num):
        await message.answer("Пожалуйста, введите корректный номер карты (ровно 4 цифры).")
        return

    await state.update_data(last_num=last_num)
    data = await state.get_data()
    telegram_id = message.from_user.id
    nickname = await get_user_info(message.from_user.id)

    card_data = {
        "cardName": data['card_name'],
        "lastNum": data['last_num']
    }

    response = requests.post(
        f"{JAVA_CARD_API_URL}/by-telegram-id?telegramId={telegram_id}", json=card_data)

    if response.status_code == 201:
        msg = await message.answer("Карта успешно добавлена!")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id,
                                    text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)
    else:
        await message.answer("Произошла ошибка при добавлении карты.")
        await message.answer(text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)

    await state.clear()

@dp.callback_query(F.data == 'my_cards')
async def show_my_cards(callback: CallbackQuery):
    telegram_id = callback.from_user.id
    card_response = requests.get(
        f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")

    if card_response.status_code == 500:
        await callback.message.answer("Произошла ошибка при получении карт. Попробуйте снова позже.")
        return

    try:
        user_cards = card_response.json()
    except:
        user_cards = False

    if not user_cards:
        await callback.message.answer("У вас нет карт.")
        return

    keyboard = InlineKeyboardMarkup(inline_keyboard=[])
    try:
        for card in user_cards:
            card_name = card['cardName']
            last_num = card['lastNum']
            if card['lastNum'] != None:
                button_text = f"{card['cardName']} ···· {card['lastNum']}"
            else:
                button_text = f"{card['cardName']}"
            keyboard.inline_keyboard.append(
                [InlineKeyboardButton(
                    text=button_text, callback_data=f"cards_{card['id']}")]
            )
        keyboard.inline_keyboard.append(
            [InlineKeyboardButton(
                text="Назад", callback_data="card_profile")]
        )
    except:
        pass

    await callback.message.edit_text(my_cards_text, reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('cards_'))
async def process_cards_selection(call: CallbackQuery, state: FSMContext):
    card_id = int(call.data.split('_')[1])
    await state.update_data(card_id=card_id)
    await call.answer("Вы выбрали карту.")
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(
            text="Редактировать", callback_data=f"action_card_edit_{card_id}")],
        [InlineKeyboardButton(
            text="Удалить", callback_data=f"action_card_delete_{card_id}")],
        [InlineKeyboardButton(
            text="Назад", callback_data="my_cards")]
    ])
    await call.message.edit_text("Выберите действие:", reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('action_card_delete_'))
async def process_delete_card(call: CallbackQuery, state: FSMContext):
    card_id = int(call.data.split('_')[3])
    delete_response = requests.delete(
        f"{JAVA_CARD_API_URL}/{card_id}")
    if delete_response.status_code == 204:
        await call.answer("Карта успешно удалена.")
        await call.message.edit_text(interaction_with_cards, reply_markup=card_action)
    else:
        await call.message.answer("Ошибка при удалении подписки.")

@dp.callback_query(lambda call: call.data.startswith('action_card_edit_'))
async def process_edit_card(call: CallbackQuery, state: FSMContext):
    card_id = int(call.data.split('_')[3])
    await state.update_data(card_id=card_id)

    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Редактировать название карты",
                              callback_data=f"edit_card_name_{card_id}")],
        [InlineKeyboardButton(text="Редактировать номер карты",
                              callback_data=f"edit_card_last_num_{card_id}")],
        [InlineKeyboardButton(text="Назад",
                              callback_data="my_cards")]
    ])
    await call.message.edit_text(select_edit, reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('edit_card_name_'))
async def edit_card_name(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text("Введите новое название карты:")
    await state.set_state("confirm_card_name")

@dp.message(StateFilter("confirm_card_name"))
async def process_edit_card_name(message: Message, state: FSMContext):
    new_card_name = message.text
    data = await state.get_data()
    card_id = data['card_id']
    nickname = await get_user_info(message.from_user.id)

    update_data = {"cardName": new_card_name}
    response = requests.patch(
        f"{JAVA_CARD_API_URL}/{card_id}", json=update_data)

    if response.status_code == 200:
        msg = await message.answer("Название карты успешно обновлено!")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)
    else:
        await message.answer("Ошибка при обновлении названия карты.")

    await state.clear()

@dp.callback_query(lambda call: call.data.startswith('edit_card_last_num_'))
async def edit_card_last_num(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text("Введите последние 4 цифры новой карты")
    await state.set_state("confirm_card_last_num")

@dp.message(StateFilter("confirm_card_last_num"))
async def process_edit_card_last_num(message: Message, state: FSMContext):
    new_last_num = message.text.strip()
    if not re.fullmatch(r"\d{4}", new_last_num):
        await message.answer("Пожалуйста, введите корректный номер карты (ровно 4 цифры).")
        return

    data = await state.get_data()
    card_id = data['card_id']
    nickname = await get_user_info(message.from_user.id)

    update_data = {"lastNum": new_last_num}
    response = requests.patch(
        f"{JAVA_CARD_API_URL}/{card_id}", json=update_data)

    if response.status_code == 200:
        msg = await message.answer("Номер карты обновлен!")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=f'''
🌟 Добро пожаловать, {nickname}!  
{profile_info} 
        ''', reply_markup=profile)
    else:
        await message.answer("Ошибка при обновлении номера карты.")
    await state.clear()

@dp.message(F.text == "Подписки")
async def shure_subs(message: Message):
    await message.answer(subs_text, reply_markup=subs)

@dp.callback_query(F.data == 'add_subs')
async def add_subscription(callback: CallbackQuery):
    await callback.message.edit_text(add_sub, reply_markup=add_subs_scan_post)

@dp.callback_query(F.data == 'add_from_list')
async def add_subscription(callback: CallbackQuery):
    response = requests.get(f"{JAVA_CATEGORY_API_URL}")
    if response.status_code == 200:
        categories = response.json()
        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for category in categories:
            keyboard.inline_keyboard.append([InlineKeyboardButton(
                text=category['categoryName'], callback_data=f'category_{category["id"]}')])
        await callback.message.edit_text(add_category, reply_markup=keyboard)
    else:
        await callback.message.answer("Ошибка при загрузке сервисов.")

@dp.callback_query(lambda call: call.data.startswith('category_'))
async def process_category_selection(call: CallbackQuery, state: FSMContext):
    category_id = int(call.data.split('_')[1])
    await state.update_data(category_id=category_id)

    response = requests.get(
        f"{JAVA_SERVICE_API_URL}/by-category-id/{category_id}")
    if response.status_code == 200:
        services = response.json()
        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for service in services:
            keyboard.inline_keyboard.append([InlineKeyboardButton(
                text=service['serviceName'], callback_data=f'service_{service["id"]}')])
        await call.answer('Вы выбрали категорию')
        await call.message.edit_text(add_service, reply_markup=keyboard)
    else:
        await call.message.answer("Ошибка при загрузке сервисов.")

@dp.callback_query(lambda call: call.data.startswith('service_'))
async def process_service_selection(call: CallbackQuery, state: FSMContext):
    service_id = int(call.data.split('_')[1])
    await state.update_data(service_id=service_id)

    response = requests.get(
        f"{JAVA_TARIFF_API_URL}/by-service-id/{service_id}")
    if response.status_code == 200:
        tariffs = response.json()
        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for tariff in tariffs:
            keyboard.inline_keyboard.append([InlineKeyboardButton(
                text=f"{tariff['durationMonths']} мес - {tariff['price']} руб.", callback_data=f'tariff_{tariff["id"]}')])
        await call.answer('Вы выбрали сервис')
        await call.message.edit_text(sub_duration, reply_markup=keyboard)
    else:
        await call.message.answer("Ошибка при загрузке тарифов.")

@dp.callback_query(lambda call: call.data.startswith('tariff_'))
async def process_tariff_selection(call: CallbackQuery, state: FSMContext):
    tariff_id = int(call.data.split('_')[1])
    await state.update_data(tariff_id=tariff_id)

    await state.set_state(SubscriptionForm.start_date)
    await call.answer('Вы выбрали тариф')
    await call.message.edit_text("Введите дату оформления подписки или последнего продления:")

@dp.message(SubscriptionForm.start_date)
async def process_start_date(message: Message, state: FSMContext):
    try:
        parsed_date = parse(message.text, dayfirst=True).date()
        await state.update_data(start_date=parsed_date)

        telegram_id = message.from_user.id
        response = requests.get(
            f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")
        if response.status_code == 200:
            user_cards = response.json()
            keyboard = InlineKeyboardMarkup(inline_keyboard=[])
            for user_card in user_cards:
                if user_card['lastNum'] != None:
                    button_text = f"{user_card['cardName']} ···· {user_card['lastNum']}"
                else:
                    button_text = f"{user_card['cardName']}"
                keyboard.inline_keyboard.append([InlineKeyboardButton(
                    text=button_text,
                    callback_data=f'card_{user_card["id"]}')])
            await message.answer("Выберите карту:", reply_markup=keyboard)
        else:
            await message.answer("Ошибка при загрузке карт.")
    except ValueError:
        await message.answer("Некорректный формат даты. Введите в формате YYYY-MM-DD.")
        return

@dp.callback_query(lambda call: call.data.startswith('card_'))
async def process_user_card_selection(call: CallbackQuery, state: FSMContext):
    user_card_id = int(call.data.split('_')[1])
    data = await state.get_data()
    telegram_id = call.from_user.id

    subscription_data = {
        'telegramId': telegram_id,
        'categoryId': data['category_id'],
        'serviceId': data['service_id'],
        'tariffId': data['tariff_id'],
        'userCardId': user_card_id,
        'date': data['start_date'].isoformat()
    }

    response = requests.post(
        f"{JAVA_SUBSCRIPTION_API_URL}/create", json=subscription_data)

    if response.status_code == 201:
        await call.answer('Подписка успешно добавлена!')
        await call.message.edit_text(subs_text, reply_markup=subs)
    else:
        await call.message.edit_text("Ошибка при добавлении подписки. Попробуйте снова.")

    await state.clear()

@dp.callback_query(F.data == 'my_subs')
async def show_my_subs(callback: CallbackQuery):
    telegram_id = callback.from_user.id
    subscriptions_response = requests.get(
        f"{JAVA_SUBSCRIPTION_API_URL}/by-telegram-id/{telegram_id}")
    custom_subscriptions_response = requests.get(
        f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/by-telegram-id/{telegram_id}")

    if subscriptions_response.status_code != 200 and custom_subscriptions_response.status_code != 200:
        await callback.message.answer("Произошла ошибка при получении подписок. Попробуйте снова позже.")
        return

    try:
        subscriptions = subscriptions_response.json()
    except:
        subscriptions = False

    try:
        custom_subscriptions = custom_subscriptions_response.json()
    except:
        custom_subscriptions = False

    if not subscriptions and not custom_subscriptions:
        await callback.message.answer("У вас нет активных подписок.")
        return

    keyboard = InlineKeyboardMarkup(inline_keyboard=[])
    try:
        for sub in subscriptions:
            service_name = sub['serviceName']
            duration = sub['tariffDuration']
            price = sub['tariffPrice']
            card_name = sub['userCardName']
            button_text = f"{service_name}: {duration} мес - {price} руб. ({card_name})"
            keyboard.inline_keyboard.append(
                [InlineKeyboardButton(
                    text=button_text, callback_data=f"subscription_{sub['id']}")]
            )
    except:
        pass

    try:
        for custom_sub in custom_subscriptions:
            service_name = custom_sub['serviceName']
            duration = custom_sub['durationMonths']
            price = custom_sub['price']
            card_name = custom_sub['userCardName']
            button_text = f"{service_name} (Кастом): {duration} мес - {price} руб. ({card_name})"
            keyboard.inline_keyboard.append(
                [InlineKeyboardButton(
                    text=button_text, callback_data=f"custom_subscription_{custom_sub['id']}")]
            )
    except:
        pass
    keyboard.inline_keyboard.append(
        [InlineKeyboardButton(text="Назад", callback_data="back_to_subs")]
    )
    await callback.message.edit_text(user_sub, reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('subscription_'))
async def process_subscription_selection(call: CallbackQuery, state: FSMContext):
    subscription_id = int(call.data.split('_')[1])
    await state.update_data(subscription_id=subscription_id)
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Редактировать",
                              callback_data=f"action_edit_{subscription_id}")],
        [InlineKeyboardButton(text="Удалить",
                              callback_data=f"action_delete_{subscription_id}")],
        [InlineKeyboardButton(text="Назад",
                              callback_data="my_subs")]
    ])
    await call.answer("Вы выбрали подписку.")
    await call.message.edit_text("Выберите действие:", reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('action_delete_'))
async def process_delete_subscription(call: CallbackQuery, state: FSMContext):
    subscription_id = int(call.data.split('_')[2])
    delete_response = requests.delete(
        f"{JAVA_SUBSCRIPTION_API_URL}/{subscription_id}")
    if delete_response.status_code == 204:
        await call.answer("Подписка успешно удалена.")
        await call.message.edit_text(subs_text, reply_markup=subs)
    else:
        await call.message.answer("Ошибка при удалении подписки.")

@dp.callback_query(lambda call: call.data.startswith('action_edit_'))
async def process_edit_action(call: CallbackQuery, state: FSMContext):
    subscription_id = int(call.data.split('_')[2])
    await state.update_data(subscription_id=subscription_id)
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Редактировать тариф",
                              callback_data=f"edit_tariff_{subscription_id}")],
        [InlineKeyboardButton(text="Редактировать карту",
                              callback_data=f"edit_card_{subscription_id}")],
        [InlineKeyboardButton(text="Редактировать дату",
                              callback_data=f"edit_date_{subscription_id}")],
        [InlineKeyboardButton(text="Назад",
                              callback_data="my_subs")]
    ])
    await call.message.edit_text(select_edit_subs, reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('edit_tariff_'))
async def process_edit_tariff(call: CallbackQuery, state: FSMContext):
    subscription_id = int(call.data.split('_')[2])
    response = requests.get(f"{JAVA_SUBSCRIPTION_API_URL}/{subscription_id}")
    if response.status_code == 200:
        subscription_data = response.json()
        service_id = subscription_data['serviceId']
        await state.update_data(subscription_id=subscription_id, service_id=service_id)

        tariffs_response = requests.get(
            f"{JAVA_TARIFF_API_URL}/by-service-id/{service_id}")
        if tariffs_response.status_code == 200:
            tariffs = tariffs_response.json()
            keyboard = InlineKeyboardMarkup(inline_keyboard=[])
            for tariff in tariffs:
                keyboard.inline_keyboard.append([
                    InlineKeyboardButton(
                        text=f"{tariff['durationMonths']} мес - {tariff['price']} руб.", callback_data=f'confirm_tariff_{tariff["id"]}')
                ])
            await call.message.edit_text(sub_duration, reply_markup=keyboard)
        else:
            await call.message.answer("Ошибка при загрузке тарифов.")
    else:
        await call.message.answer("Ошибка при загрузке данных подписки.")

@dp.callback_query(lambda call: call.data.startswith('edit_card_'))
async def edit_card(call: CallbackQuery, state: FSMContext):
    subscription_id = int(call.data.split('_')[2])
    await state.update_data(subscription_id=subscription_id)

    telegram_id = call.from_user.id
    response = requests.get(
        f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")
    if response.status_code == 200:
        user_cards = response.json()

        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for user_card in user_cards:
            if user_card['lastNum'] != None:
                button_text = f"{user_card['cardName']} ···· {user_card['lastNum']}"
            else:
                button_text = f"{user_card['cardName']}"

            keyboard.inline_keyboard.append([InlineKeyboardButton(
                    text=button_text,
                    callback_data=f'confirm_card_{user_card["id"]}')])
        await call.message.edit_text("Выберите новую карту:", reply_markup=keyboard)
    else:
        await call.message.answer("Ошибка при загрузке карт.")

@dp.callback_query(lambda call: call.data.startswith('edit_date_'))
async def process_edit_date(call: CallbackQuery, state: FSMContext):
    subscription_id = int(call.data.split('_')[2])
    await state.update_data(subscription_id=subscription_id)

    await call.message.edit_text("Введите новую дату в формате `ДД.ММ.ГГГГ` или `ГГГГ ММ ДД`:")
    await state.set_state("waiting_for_date")

@dp.message(StateFilter("waiting_for_date"))
async def process_new_date(message: Message, state: FSMContext):
    try:
        new_date = parse(message.text, dayfirst=True).date()
        data = await state.get_data()
        subscription_id = data.get("subscription_id")
        update_data = {"date": new_date.isoformat()}
        response = requests.patch(
            f"{JAVA_SUBSCRIPTION_API_URL}/{subscription_id}",
            json=update_data
        )

        if response.status_code == 200:
            msg = await message.answer("Дата успешно обновлена!")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
        else:
            await message.answer(f"Ошибка при обновлении даты: {response.text}")

    except ValueError:
        await message.answer("Неверный формат даты. Попробуйте снова (например, `01.12.2024` или `2024-12-01`).")
        return
    except Exception as e:
        await message.answer(f"Произошла ошибка: {e}")

    await state.clear()

@dp.callback_query(lambda call: call.data.startswith('edit_all_'))
async def edit_all(call: CallbackQuery, state: FSMContext):
    subscription_id = int(call.data.split('_')[2])
    await state.update_data(subscription_id=subscription_id, edit_all=True)
    await process_edit_tariff(call, state)

@dp.callback_query(lambda call: call.data.startswith('confirm_tariff_'))
async def update_tariff(call: CallbackQuery, state: FSMContext):
    tariff_id = int(call.data.split('_')[2])
    data = await state.get_data()
    await state.update_data(tariff_id=tariff_id)
    subscription_id = data.get('subscription_id')

    if data.get('edit_all'):
        await edit_card(call, state)
    else:
        subscription_id = data['subscription_id']
        update_data = {"tariffId": tariff_id}

        response = requests.patch(
            f"{JAVA_SUBSCRIPTION_API_URL}/{subscription_id}", json=update_data)

        if response.status_code == 200:
            await call.answer("Тариф успешно обновлен!")
            await call.message.edit_text(subs_text, reply_markup=subs)
        else:
            await call.message.answer("Ошибка при обновлении тарифа.")

        await state.clear()

@dp.callback_query(lambda call: call.data.startswith('confirm_card_'))
async def update_card(call: CallbackQuery, state: FSMContext):
    user_card_id = int(call.data.split('_')[2])
    data = await state.get_data()
    await state.update_data(user_card_id=user_card_id)

    subscription_id = data.get('subscription_id')
    if not subscription_id:
        await call.message.answer("Ошибка: ID подписки не найден.")
        return

    update_data = {"userCardId": user_card_id}
    if 'tariff_id' in data:
        update_data['tariffId'] = data['tariff_id']
    response = requests.patch(
        f"{JAVA_SUBSCRIPTION_API_URL}/{subscription_id}", json=update_data)

    if response.status_code == 200:
        if 'tariff_id' in data:
            await call.answer("Тариф и карта успешно обновлены!")
        else:
            await call.answer("Карта успешно обновлена!")
            await call.message.edit_text(subs_text, reply_markup=subs)
    else:
        await call.message.answer("Ошибка при обновлении карты.")

    await state.clear()

@dp.callback_query(lambda call: call.data.startswith('custom_subscription_'))
async def process_custom_subscription_selection(call: CallbackQuery, state: FSMContext):
    custom_subscription_id = int(call.data.split('_')[2])
    await state.update_data(custom_subscription_id=custom_subscription_id)
    await call.answer("Вы выбрали подписку.")
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(
            text="Редактировать", callback_data=f"action_custom_edit_{custom_subscription_id}")],
        [InlineKeyboardButton(
            text="Удалить", callback_data=f"action_custom_delete_{custom_subscription_id}")],
        [InlineKeyboardButton(
            text="Назад", callback_data="my_subs")]
    ])
    await call.message.edit_text("Выберите действие:", reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('action_custom_delete_'))
async def process_delete_custom_subscription(call: CallbackQuery, state: FSMContext):
    custom_subscription_id = int(call.data.split('_')[3])
    delete_response = requests.delete(
        f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/{custom_subscription_id}")
    if delete_response.status_code == 204:
        await call.answer("Подписка успешно удалена.")
        await call.message.edit_text(subs_text, reply_markup=subs)
    else:
        await call.message.answer("Ошибка при удалении подписки.")

@dp.callback_query(lambda call: call.data.startswith('action_custom_edit_'))
async def process_edit_custom_action(call: CallbackQuery, state: FSMContext):
    custom_subscription_id = int(call.data.split('_')[3])
    await state.update_data(custom_subscription_id=custom_subscription_id)
    keyboard = InlineKeyboardMarkup(inline_keyboard=[
        [InlineKeyboardButton(text="Редактировать навзание сервиса",
                              callback_data=f"edit_custom_servname_{custom_subscription_id}")],
        [InlineKeyboardButton(text="Редактировать длительность подписки",
                              callback_data=f"edit_custom_durmonths_{custom_subscription_id}")],
        [InlineKeyboardButton(text="Редактировать цену",
                              callback_data=f"edit_custom_price_{custom_subscription_id}")],
        [InlineKeyboardButton(text="Редактировать карту",
                              callback_data=f"edit_custom_card_{custom_subscription_id}")],
        [InlineKeyboardButton(text="Редактировать дату",
                              callback_data=f"edit_custom_date_{custom_subscription_id}")],
        [InlineKeyboardButton(text="Назад",
                              callback_data="my_subs")]
    ])
    await call.message.edit_text(select_edit_custom_subs, reply_markup=keyboard)

@dp.callback_query(lambda call: call.data.startswith('edit_custom_servname_'))
async def edit_custom_service_name(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text(add_service_custom)
    await state.set_state("custom_edit_service_name")

@dp.message(StateFilter("custom_edit_service_name"))
async def process_custom_service_name_edit(message: Message, state: FSMContext):
    new_service_name = message.text
    data = await state.get_data()
    custom_subscription_id = data['custom_subscription_id']

    update_data = {"serviceName": new_service_name}
    response = requests.patch(
        f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/{custom_subscription_id}", json=update_data)

    if response.status_code == 200:
        msg = await message.answer("Название сервиса успешно обновлено!")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
    else:
        msg = await message.answer("Ошибка при обновлении названия сервиса.")
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
        await asyncio.sleep(2)
        await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)

    await state.clear()

@dp.callback_query(lambda call: call.data.startswith('edit_custom_durmonths_'))
async def edit_custom_duration_months(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text("Введите новую длительность подписки (в месяцах):")
    await state.set_state("custom_edit_duration_months")

@dp.message(StateFilter("custom_edit_duration_months"))
async def process_custom_duration_months_edit(message: Message, state: FSMContext):
    try:
        new_duration = int(message.text)
        if new_duration <= 0:
            raise ValueError("Duration must be positive.")

        data = await state.get_data()
        custom_subscription_id = data['custom_subscription_id']

        update_data = {"durationMonths": new_duration}
        response = requests.patch(
            f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/{custom_subscription_id}", json=update_data)

        if response.status_code == 200:
            msg = await message.answer("Длительность подписки успешно обновлена!")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
        else:
            msg = await message.answer("Ошибка при обновлении длительности подписки.")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
    except ValueError:
        await message.answer("Пожалуйста, введите корректное число.")
    finally:
        await state.clear()

@dp.callback_query(lambda call: call.data.startswith('edit_custom_price_'))
async def edit_custom_price(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text("Введите новую цену подписки (в рублях):")
    await state.set_state("custom_edit_price")

@dp.message(StateFilter("custom_edit_price"))
async def process_custom_price_edit(message: Message, state: FSMContext):
    try:
        new_price = float(message.text)
        if new_price <= 0:
            raise ValueError("Price must be positive.")

        data = await state.get_data()
        custom_subscription_id = data['custom_subscription_id']

        update_data = {"price": new_price}
        response = requests.patch(
            f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/{custom_subscription_id}", json=update_data)

        if response.status_code == 200:
            msg = await message.answer("Цена подписки успешно обновлена!")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
        else:
            msg = await message.answer("Ошибка при обновлении цены подписки.")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
    except ValueError:
        await message.answer("Пожалуйста, введите корректное число.")
    finally:
        await state.clear()

@dp.callback_query(lambda call: call.data.startswith('edit_custom_card_'))
async def edit_custom_card(call: CallbackQuery, state: FSMContext):
    telegram_id = call.from_user.id
    response = requests.get(
        f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")

    if response.status_code == 200:
        user_cards = response.json()

        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for user_card in user_cards:
            if user_card['lastNum'] != None:
                button_text = f"{user_card['cardName']} ···· {user_card['lastNum']}"
            else:
                button_text = f"{user_card['cardName']}"

            keyboard.inline_keyboard.append([InlineKeyboardButton(
                    text=button_text,
                    callback_data=f'confirm_custom_card_{user_card["id"]}')])
        await call.message.edit_text("Выберите новую карту:", reply_markup=keyboard)
    else:
        await call.message.answer("Ошибка при загрузке карт.")
        time.sleep(2)
        bot.delete_message(call.message.chat.id, call.message.message_id)

@dp.callback_query(lambda call: call.data.startswith('confirm_custom_card_'))
async def process_custom_card_edit(call: CallbackQuery, state: FSMContext):
    new_card_id = int(call.data.split('_')[3])
    data = await state.get_data()
    custom_subscription_id = data['custom_subscription_id']

    update_data = {"userCardId": new_card_id}
    response = requests.patch(
        f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/{custom_subscription_id}", json=update_data)

    if response.status_code == 200:
        await call.message.edit_text("Карта успешно обновлена!")
        await asyncio.sleep(2)
        await call.message.edit_text(text=subs_text, reply_markup=subs)
    else:
        await call.message.answer("Ошибка при обновлении карты.")
        time.sleep(2)
        bot.delete_message(call.message.chat.id, call.message.message_id)

    await state.clear()

@dp.callback_query(lambda call: call.data.startswith('edit_custom_date_'))
async def process_custom_date_edit(call: CallbackQuery, state: FSMContext):
    await call.message.edit_text("Введите новую дату в формате `ДД.ММ.ГГГГ` или `ГГГГ ММ ДД`:")
    await state.set_state("custom_edit_date")

@dp.message(StateFilter("custom_edit_date"))
async def process_date_custom_edit(message: Message, state: FSMContext):
    try:
        new_date = parse(message.text, dayfirst=True).date()
        data = await state.get_data()
        custom_subscription_id = data['custom_subscription_id']
        update_data = {"date": new_date.isoformat()}
        response = requests.patch(
            f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/{custom_subscription_id}",
            json=update_data
        )

        if response.status_code == 200:
            msg = await message.answer("Дата успешно обновлена!")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
        else:
            await message.answer(f"Ошибка при обновлении даты: {response.text}")

    except ValueError:
        await message.answer("Неверный формат даты. Попробуйте снова (например, `01.12.2024` или `2024-12-01`).")
        return
    except Exception as e:
        await message.answer(f"Произошла ошибка: {e}")

    await state.clear()

@dp.callback_query(F.data == "add_custom")
async def add_custom_subscription(callback: CallbackQuery, state: FSMContext):
    telegram_id = callback.from_user.id
    response = requests.get(
        f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")

    if response.status_code == 200:
        user_cards = response.json()
        if not user_cards:
            await callback.message.answer("У вас нет добавленных карт. Пожалуйста, добавьте карту перед созданием кастомной подписки.")
            return

        await state.update_data(telegram_id=telegram_id)
        await state.set_state(CustomSubscriptionForm.service_name)
        await callback.message.edit_text(add_service_custom)
    else:
        await callback.message.answer("Ошибка при проверке карт. Попробуйте позже.")

@dp.message(CustomSubscriptionForm.service_name)
async def process_service_name(message: Message, state: FSMContext):
    await state.update_data(service_name=message.text)
    await state.set_state(CustomSubscriptionForm.duration_months)
    await message.answer("Введите продолжительность подписки в месяцах.")
    await bot.delete_message(message.chat.id, message.message_id)
    await bot.delete_message(message.chat.id, message.message_id - 1)

@dp.message(CustomSubscriptionForm.duration_months)
async def process_duration_months(message: Message, state: FSMContext):
    if not message.text.isdigit():
        await message.answer("Продолжительность должна быть числом. Попробуйте снова.")
        return

    await state.update_data(duration_months=int(message.text))
    await state.set_state(CustomSubscriptionForm.price)
    await message.answer("Введите стоимость подписки (в формате: 123.45).")
    await bot.delete_message(message.chat.id, message.message_id)
    await bot.delete_message(message.chat.id, message.message_id - 1)

@dp.message(CustomSubscriptionForm.price)
async def process_price(message: Message, state: FSMContext):
    try:
        price = float(message.text)
        await state.update_data(price=price)
    except ValueError:
        await message.answer("Некорректный формат цены. Попробуйте снова.")
        return

    telegram_id = (await state.get_data())['telegram_id']
    response = requests.get(
        f"{JAVA_CARD_API_URL}/by-telegram-id/{telegram_id}")

    if response.status_code == 200:
        user_cards = response.json()

        keyboard = InlineKeyboardMarkup(inline_keyboard=[])
        for card in user_cards:
            if card['lastNum'] != None:
                button_text = f"{card['cardName']} ···· {card['lastNum']}"
            else:
                button_text = f"{card['cardName']}"

            keyboard.inline_keyboard.append([InlineKeyboardButton(
                    text=button_text,
                    callback_data=f'cardforcustom_{card["id"]}')])
        await state.set_state(CustomSubscriptionForm.user_card_id)
        await message.answer("Выберите карту для подписки:", reply_markup=keyboard)
        await bot.delete_message(message.chat.id, message.message_id)
        await bot.delete_message(message.chat.id, message.message_id - 1)
    else:
        await message.answer("Ошибка при загрузке карт.")


@dp.callback_query(lambda call: call.data.startswith('cardforcustom_'))
async def process_user_card_selection(call: CallbackQuery, state: FSMContext):
    user_card_id = int(call.data.split('_')[1])
    await state.update_data(user_card_id=user_card_id)
    await state.set_state(CustomSubscriptionForm.date)
    msg = await call.message.answer("Введите дату начала подписки в формате ГГГГ ММ ДД или ДД.ММ.ГГГГ:")
    await bot.delete_message(msg.chat.id, msg.message_id - 1)


@dp.message(CustomSubscriptionForm.date)
async def process_set_date(message: Message, state: FSMContext):
    date_text = message.text
    try:
        date = parse(message.text, dayfirst=True).date()
        await state.update_data(date=date)

        data = await state.get_data()
        subscription_data = {
            "userId": data['telegram_id'],
            "serviceName": data['service_name'],
            "durationMonths": data['duration_months'],
            "price": data['price'],
            "userCardId": data['user_card_id'],
            "date": data['date'].isoformat()
        }

        response = requests.post(
            f"{JAVA_CUSTOM_SUBSCRIPTION_API_URL}/create-custom-subs", json=subscription_data)

        if response.status_code == 201:
            msg = await message.answer("Кастомная подписка успешно создана!")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
        else:
            msg = await message.answer("Ошибка при создании подписки. Попробуйте позже.")
            await bot.delete_message(message.chat.id, message.message_id)
            await bot.delete_message(message.chat.id, message.message_id - 1)
            await asyncio.sleep(2)
            await bot.edit_message_text(chat_id=message.chat.id, message_id=msg.message_id, text=subs_text, reply_markup=subs)
    except ValueError:
        await message.answer("Некорректный формат даты. Пожалуйста, введите дату в формате ГГГГ-ММ-ДД.")
    await state.clear()

@dp.message(Command('cancel'))
async def cancel_handler(message: Message, state: FSMContext):
    await state.clear()
    await message.answer("Действие отменено.")

async def scheduler():

    while True:
        await async_main()
        await asyncio.sleep(86400)

def worker():
    asyncio.run((scheduler()))

async def main():
    process = multiprocessing.Process(target=worker)
    process.start()
    await dp.start_polling(bot)
    process.join()

if __name__ == '__main__':
    asyncio.run(main())
